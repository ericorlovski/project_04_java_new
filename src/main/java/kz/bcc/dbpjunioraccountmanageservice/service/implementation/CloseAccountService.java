package kz.bcc.dbpjunioraccountmanageservice.service.implementation;

import kz.bcc.dbpjunioraccountmanageservice.exception.RequestErrorException;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.*;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.request.ColvirRequestPayload;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.request.ColvirRequestWrapper;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.response.ColvirResponsePayload;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.juniorAuth.response.ColvirResponsePayloadV2;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.response.MciResponsePayload;
import kz.bcc.dbpjunioraccountmanageservice.model.entity.CloseAcc;
import kz.bcc.dbpjunioraccountmanageservice.model.entity.CloseAccHistory;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.*;
import kz.bcc.dbpjunioraccountmanageservice.model.repository.CloseAccHistoryRepository;
import kz.bcc.dbpjunioraccountmanageservice.model.repository.CloseAccRepository;
import kz.bcc.dbpjunioraccountmanageservice.service.*;
import kz.bcc.dbpjunioraccountmanageservice.service.utls.Utils;
import kz.bcc.dbpjunioraccountmanageservice.web.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Log4j2
public class CloseAccountService implements ICloseAccountService {

    private final IBankRequestService bankRequestService;

    private final CloseAccRepository closeAccRepository;

    private final IJuniorAuthService juniorAuthService;

    private final IMciService mciService;

    private final IReissueAccountService reissueAccountService;

    private final IColvirService colvirService;

    private final CloseAccHistoryRepository closeAccHistoryRepository;

    @Override
    public GenericResponse<JuniorProductsDto> getJuniorProducts(HoldDto dto) { //Метод проверки наличия детских продуктов

        String token = colvirService.getAuthToken(BearerTokenType.COMMON); //запрос токена в keycloack
        log.info("Successful request a token for COLVIR, with idn: {}, method getJuniorProducts", dto.getIdn());

        val object = new ColvirRequestWrapper(BankPackage.COLVIR_JUNIOR_PRODUCTS.getDescription());
        val payload = new ColvirRequestPayload(dto.getIdn(), null, null,
                null, null, null, null, null);
        object.setPayload(payload);

        ColvirResponsePayload response = colvirService.getColvirRestTemplate(object, token); //запрос в Колвир
        log.info("Successful request to the system COLVIR, with idn: {}, method getJuniorProducts", dto.getIdn());

        val product = new JuniorProductsDto();

        if (!response.getSuccess()) {
            product.setAccount(true);
            product.setDeposit(true);
            product.setCashback(true);
        } else {
            product.setAccount(false);
            product.setDeposit(false);
            product.setCashback(false);
        }

        return new GenericResponse<>(product);
    }

    //Метод для блокировки и отправки на согласование родителем карты Junior
    @Override
    public GenericResponse<Boolean> closeJuniorCardRequest(CloseDto dto) {
        //получение сущности ребенка и его родителя по сотовому ребенка
        ColvirResponsePayloadV2 junior = getJuniorRbsByPhone(dto.getJuniorPhone());

        if (junior == null) {
            return GenericResponse.error(MessageCode.PARENT_OR_CHILD_ENTITY_EMPTY);
        }

        if (isMatchingRbs(junior, dto)) { //матчинг RBS из колвира и RBS запроса
            if (blockJuniorCardForClose(dto, junior, junior.getParent())) { //блокировка карты
                Optional<CloseAcc> closeAccOptional = closeAccRepository.findFirstByIdnAndActiveOrderByIdDesc
                        (dto.getIdn(), true);

                if (closeAccOptional.isPresent()) {
                    CloseAcc closeAcc = closeAccOptional.get();

                    try {
                        String firstName = closeAcc.getJuniorFullName().split("\\s+")[0];

                        //создание уведомления для родителя в MCI
                        MciResponsePayload closeNotify = sendNotifyToParent(closeAcc, firstName);
                        if (!closeNotify.getStatus()) {
                            handleNotifyError(dto, closeAcc, closeNotify);
                            return GenericResponse.error(MessageCode.MCI_NOTIFY_SERVICE_ERROR);
                        }

                        closeAcc.setCloseAccStatus(StatusType.PARENT_APPROVAL);
                        closeAccRepository.save(closeAcc);

                        //сохранения статуса для ведения аналитики заявки
                        pushCloseAccHistoryTable(dto.getIdn(), StatusType.PARENT_APPROVAL, null);

                        return new GenericResponse<>(closeNotify.getStatus());
                    } catch (Exception e) {
                        //обработка ошики создания уведомления
                        handleNotifyError(dto, closeAcc, null);
                        return GenericResponse.error(MessageCode.CARD_CLOSE_INITIALIZING_ERROR);
                    }
                }
            } else {
                return GenericResponse.error(MessageCode.CARD_BLOCK_ERROR);
            }
        }

        return GenericResponse.error(MessageCode.NO_MATCHING_CHILD_ERROR);
    }

    private ColvirResponsePayloadV2 getJuniorRbsByPhone(String juniorPhone) {
        return juniorAuthService.getUserDataByPhone(
                juniorAuthService.getUserInfoRequest(juniorPhone, UserType.JUNIOR));
    }

    //Метод отклонения закрытия карты родителем
    @Override
    public GenericResponse<Boolean> rejectRequestCloseCard(HoldDto dto) {

        val closeAcc = closeAccRepository.findFirstByIdnAndActiveOrderByIdDesc(dto.getIdn(), true);

        if (closeAcc.isPresent()) {
            //разблокировка карты
            reissueAccountService.unblockJuniorCard(dto.getIdn(), MessageCode.PARENT_REJECTED_MESSAGE.getDescription());

            //пуш уведомление об отклонении закрытия родителем junior
            bankRequestService.sendMessage(String.valueOf(closeAcc.get().getJuniorPhone()), closeAcc.get().getJuniorFullName(),
                    UserType.JUNIOR, PushSmsDescCode.SERVICE_NAME.getDescription(),
                    getPushText(String.valueOf(closeAcc.get().getJuniorPhone()), "PUSH_REJECTED_JB")
                            + closeAcc.get().getParentFullName(),
                    PushSmsType.JUNIOR_PUSH);

            //сохранение записи в историю заявки закрытия счета
            pushCloseAccHistoryTable(dto.getIdn(), StatusType.PARENT_REJECTED, null);

            //закрытие заявки
            closeAcc.get().setCloseAccStatus(StatusType.PARENT_REJECTED);
            closeAcc.get().setActive(false);
            closeAccRepository.save(closeAcc.get());

            return new GenericResponse<>(true);
        }

        return GenericResponse.error(MessageCode.CLIENT_NOT_FOUND_ERROR);
    }

    //Метод блокировки карты для закрытия
    private Boolean blockJuniorCardForClose(CloseDto dto, ColvirResponsePayloadV2 child, ColvirResponsePayloadV2 parent) {

        val closeAcc = closeAccRepository.findFirstByIdnAndActiveOrderByIdDesc(dto.getIdn(), true);
        if (closeAcc.isPresent()) {
            throw new RequestErrorException(MessageCode.NAME_ALREADY_EXIST.getDescription());
        }

        ColvirResponsePayload block = performColvirRequestToClose(dto, BankPackage.COLVIR_CARD_BLOCK,
                "blockJuniorCardForClose");

        if (block.getSuccess()) {
            log.info("The card was successfully blocked, with idn: {}", dto.getIdn());

            pushCloseAccTable(dto, child.getPhone(),
                    Utils.getFullName(child.getFirstName(), child.getLastName(), child.getMiddleName()),
                    Utils.getFullName(parent.getFirstName(), parent.getLastName(), parent.getMiddleName()), parent.getPhone());
            //сохранение карты в очередь для проверки статуса закрытия
        }

        return block.getSuccess();
    }

    //Метод изменения статуса заявки для последующего запроса в Колвир по джобе
    @Override
    public GenericResponse<Boolean> updCloseStatusForClose(ApproveCloseDto dto) {

        val closeAcc = closeAccRepository.findFirstByIdnAndActiveOrderByIdDesc(dto.getIdn(), true);
        if (closeAcc.isPresent()) {
            closeAcc.get().setCloseAccStatus(StatusType.CLOSE_START);
            closeAcc.get().setPayOutAccount(dto.getPayOutAcc());
            closeAccRepository.save(closeAcc.get());

            //сохранение записи в историю заявки закрытия счета
            pushCloseAccHistoryTable(dto.getIdn(), StatusType.CLOSE_START, null);

            return new GenericResponse<>(true);
        }

        return GenericResponse.error(MessageCode.CLIENT_NOT_FOUND_ERROR);
    }

    //Метод отправки карты в Colvir очередь закрытия
    public void closeJuniorCardFunction(CloseAcc closeAcc) {
        CloseDto dto = buildCloseDto(closeAcc.getIdn(), closeAcc.getAccBalance(), String.valueOf(closeAcc.getJuniorPhone()),
                closeAcc.getCloseReason(), closeAcc.getPayOutAccount());

        try {
            //запрос в колвир на закрытие карты
            ColvirResponsePayload closeRequest = performColvirRequestToClose(dto,
                    BankPackage.COLVIR_CARD_CLOSE, "closeJuniorCardFunction");

            if (closeRequest.getSuccess()) {
                //обработка успеха закрытия карты
                handleSuccessfulClosure(dto, closeAcc);
            } else {
                //обработка ошибки закрытия карты
                handleClosureError(dto, closeAcc, closeRequest.toString());
            }
        } catch (Exception e) {
            //обработка ошибки Colvir
            handleClosureError(dto, closeAcc, MessageCode.CARD_CLOSE_ERROR.getDescription());
        }
    }

    //Метод успешности закрытия карты
    private void handleSuccessfulClosure(CloseDto dto, CloseAcc closeAcc) {
        // сохранение записи в историю заявки закрытия счета
        pushCloseAccHistoryTable(dto.getIdn(), StatusType.CLOSE_CARD_COMPLETE, null);

        String pushTextKey = (Objects.equals(closeAcc.getAccBalance(), "0")) ? "PUSH_CLOSE_BCC" : "PUSH_CLOSE_BALANCE_BCC";

        //пуш уведомление о закртии карты parent
        bankRequestService.sendMessage(String.valueOf(closeAcc.getParentPhone()), closeAcc.getJuniorFullName(),
                UserType.PARENT, PushSmsDescCode.SERVICE_NAME.getDescription(),
                getPushText(String.valueOf(closeAcc.getParentPhone()), pushTextKey),
                PushSmsType.PARENT_PUSH);
        log.info("The card was successfully closed, with idn: {}", dto.getIdn());

        closeAcc.setCloseAccStatus(StatusType.CLOSE_CARD_COMPLETE);
        closeAcc.setActive(false);
        closeAccRepository.save(closeAcc);
    }

    //Метод ошибки закрытия карты
    private void handleClosureError(CloseDto dto, CloseAcc closeAcc, String error) {

        // сохранение записи в историю заявки закрытия счета
        pushCloseAccHistoryTable(dto.getIdn(), StatusType.CLOSE_CARD_ERROR, error);

        String notClosedParentText = getPushText(String.valueOf(closeAcc.getParentPhone()), "PUSH_NOT_CLOSED_BCC");
        String notClosedJuniorText = getPushText(String.valueOf(closeAcc.getJuniorPhone()), "PUSH_NOT_CLOSED_JB");

        // пуш уведомление о незакрытии для parent
        bankRequestService.sendMessage(String.valueOf(closeAcc.getParentPhone()), closeAcc.getJuniorFullName(),
                UserType.PARENT, PushSmsDescCode.SERVICE_NAME.getDescription(),
                notClosedParentText, PushSmsType.PARENT_PUSH);

        // пуш уведомление о не закрытии для junior
        bankRequestService.sendMessage(String.valueOf(closeAcc.getJuniorPhone()), closeAcc.getJuniorFullName(),
                UserType.JUNIOR, PushSmsDescCode.SERVICE_NAME.getDescription(),
                notClosedJuniorText, PushSmsType.JUNIOR_PUSH);

        // разблокировка карты
        reissueAccountService.unblockJuniorCard(dto.getIdn(), MessageCode.CARD_CLOSE_ERROR.getDescription());

        closeAcc.setErrorMessage(error);
        closeAcc.setCloseAccStatus(StatusType.CLOSE_CARD_ERROR);
        closeAcc.setActive(false);
        closeAccRepository.save(closeAcc);
    }

    private ColvirResponsePayload performColvirRequestToClose(CloseDto dto, BankPackage colvirPackage, String method) {

        String token = colvirService.getAuthToken(BearerTokenType.COMMON); //запрос токена в keycloack
        log.info("Successful request a token for COLVIR, with idn: {}, method {}",
                dto.getIdn(), method);

        val object = new ColvirRequestWrapper(colvirPackage.getDescription());
        val payload = new ColvirRequestPayload(dto.getIdn(), MessageCode.BLOCK_CARD_TO_CLOSE_CODE.getDescription(),
                null, null, null, MessageCode.REISSUE_CARD_CODE.getDescription(),
                dto.getPayOutAcc(), MessageCode.CLOSE_CARD_CODE.getDescription());

        if (Integer.parseInt(dto.getAccBalance()) < 500 && dto.getPayOutAcc() == null) {
            payload.setPayoutfl(MessageCode.REISSUE_CARD_CODE.getDescription());
        }
        object.setPayload(payload);

        ColvirResponsePayload response = colvirService.getColvirRestTemplate(object, token); //запрос в Колвир
        log.info("Successful request to the system COLVIR, with idn: {}, method {}", dto.getIdn(), method);

        return response;
    }

    //Метод подтверждения матчинга RBS из колвира и RBS запроса
    private boolean isMatchingRbs(ColvirResponsePayloadV2 child, CloseDto dto) {
        return child.getRbs().equals(dto.getIdn()) || child.getRbsList().contains(dto.getIdn());
    }

    //Метод генерации уведомления для родителя
    private MciResponsePayload sendNotifyToParent(CloseAcc closeAcc, String firstName) {
        return mciService.pushNotifyAboutCloseAcc(firstName,
                closeAcc.getAccBalance(), closeAcc.getCloseReason(),
                String.valueOf(closeAcc.getJuniorPhone()));
    }

    //Метод ошибки генерации уведомления для родителя
    private void handleNotifyError(CloseDto dto, CloseAcc closeAcc, MciResponsePayload closeNotify) {
        //занесение аналитики ошибки
        pushCloseAccHistoryTable(dto.getIdn(), StatusType.CLOSE_CARD_ERROR,
                closeNotify != null ? MessageCode.MCI_NOTIFY_SERVICE_ERROR.getDescription() :
                        MessageCode.CARD_CLOSE_INITIALIZING_ERROR.getDescription());
        
        //разблокировка карты
        reissueAccountService.unblockJuniorCard(dto.getIdn(),
                MessageCode.MCI_NOTIFY_SERVICE_ERROR.getDescription());

        //закрытие заявки
        closeAcc.setErrorMessage(closeNotify != null ? closeNotify.toString() :
                MessageCode.CARD_CLOSE_INITIALIZING_ERROR.getDescription());
        closeAcc.setCloseAccStatus(StatusType.CLOSE_CARD_ERROR);
        closeAcc.setActive(false);
        closeAccRepository.save(closeAcc);
    }

    //Метод занесения клиента в таблицу очереди закрытия карты
    private void pushCloseAccTable(CloseDto dto, String juniorPhone, String juniorFullName, String parentFullName,
                                   String parentPhone) {

        val closeTable = new CloseAcc();
        closeTable.setJuniorPhone(Long.valueOf(juniorPhone));
        closeTable.setJuniorFullName(juniorFullName);
        closeTable.setParentPhone(Long.valueOf(parentPhone));
        closeTable.setParentFullName(parentFullName);
        closeTable.setIdn(dto.getIdn());
        closeTable.setBlockReason(MessageCode.BLOCK_CARD_TO_CLOSE_CODE.getDescription());
        closeTable.setCloseAccStatus(StatusType.BLOCK_CARD);
        closeTable.setAccBalance(dto.getAccBalance());
        closeTable.setCloseReason(dto.getCloseReason());
        closeAccRepository.save(closeTable);

        //сохранение записи в историю заявки закрытия счета
        pushCloseAccHistoryTable(dto.getIdn(), StatusType.BLOCK_CARD, null);
    }

    //Метод ведения аналитики истории заявки закрытия карточного счета
    private void pushCloseAccHistoryTable(String idn, StatusType type, String result) {

        val closeAcc = closeAccRepository.findFirstByIdnAndActiveOrderByIdDesc(idn, true);

        if (closeAcc.isPresent()) {
            val closeAccHistory = new CloseAccHistory();
            closeAccHistory.setIdn(idn);
            closeAccHistory.setCloseAccStatus(type);
            closeAccHistory.setResultDescription(result);
            closeAccHistory.setCloseAccId(closeAcc.get().getId());

            closeAccHistoryRepository.save(closeAccHistory);
        }
    }

    private CloseDto buildCloseDto(String idn, String accBalance, String juniorPhone, String closeReason, String payOutAcc) {

        val closeDto = new CloseDto();
        closeDto.setIdn(idn);
        closeDto.setAccBalance(accBalance);
        closeDto.setJuniorPhone(juniorPhone);
        closeDto.setCloseReason(closeReason);
        closeDto.setPayOutAcc(payOutAcc);

        return closeDto;
    }

    private String getPushText(String juniorPhone, String messageType) {

        switch (messageType) {
            case "PUSH_NOT_CLOSED_JB", "PUSH_REJECTED_JB" -> {
                MciResponsePayload junior = mciService.getJuniorLangByJuniorLogin(juniorPhone);
                return Utils.getPushText(messageType, junior.getLang());
            }
            case "PUSH_NOT_CLOSED_BCC", "PUSH_CLOSE_BCC", "PUSH_CLOSE_BALANCE_BCC" -> {
                MciResponsePayload parent = mciService.getParentLangByJuniorLogin(juniorPhone);
                return Utils.getPushText(messageType, parent.getLang());
            }
        }

        return null;
    }
}
