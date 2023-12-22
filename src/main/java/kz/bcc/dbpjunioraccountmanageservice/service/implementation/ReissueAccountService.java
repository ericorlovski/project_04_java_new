package kz.bcc.dbpjunioraccountmanageservice.service.implementation;

import kz.bcc.dbpjunioraccountmanageservice.exception.RequestErrorException;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.*;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.request.ColvirRequestPayload;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.request.ColvirRequestWrapper;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.response.ColvirResponsePayload;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.juniorAuth.response.ColvirResponsePayloadV2;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.response.MciResponsePayload;
import kz.bcc.dbpjunioraccountmanageservice.model.entity.Reissue;
import kz.bcc.dbpjunioraccountmanageservice.model.entity.ReissueHistory;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.*;
import kz.bcc.dbpjunioraccountmanageservice.model.repository.ReissueHistoryRepository;
import kz.bcc.dbpjunioraccountmanageservice.model.repository.ReissueRepository;
import kz.bcc.dbpjunioraccountmanageservice.service.*;
import kz.bcc.dbpjunioraccountmanageservice.service.utls.Utils;
import kz.bcc.dbpjunioraccountmanageservice.web.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Log4j2
public class ReissueAccountService implements IReissueAccountService {

    private final IBankRequestService bankRequestService;

    private final ReissueRepository reissueRepository;

    private final ReissueHistoryRepository reissueHistoryRepository;

    private final IJuniorAuthService juniorAuthService;

    private final IMciService mciService;

    private final IColvirService colvirService;

    @Override
    public GenericResponse<CheckHoldDto> getCardHoldStatus (HoldDto dto) { //Метод проверки холдов на карте

        String token = colvirService.getAuthToken(BearerTokenType.COMMON); //запрос токена в keycloack
        log.info("Successful request a token for COLVIR, with idn: {}, method getCardHoldStatus" , dto.getIdn());

        val object = new ColvirRequestWrapper(BankPackage.COLVIR_CARD_HOLD_STATUS.getDescription());
        val payload = new ColvirRequestPayload(dto.getIdn(), null, null,
                null, null, null, null, null);
        object.setPayload(payload);

        ColvirResponsePayload response = colvirService.getColvirRestTemplate(object, token); //запрос в Колвир
        log.info("Successful request to the system COLVIR, with idn: {}, method getCardHoldStatus" , dto.getIdn());

        val checkHold = new CheckHoldDto();

        //статус false - в случае, если холдов на карте нет
        if (!response.getSuccess()) {
            checkHold.setSuccess(false);
        } else {
            checkHold.setSuccess(true);
            checkHold.setErrorType(response.getCode());
        }

        return new GenericResponse<>(checkHold);
    }

    @Override
    public GenericResponse<Boolean> unblockJuniorCard (String idn, String unblockReasonDscr) { //Метод разблокировки карты

        String token = colvirService.getAuthToken(BearerTokenType.COMMON); //запрос токена в keycloack
        log.info("Successful request a token for COLVIR, with idn: {}, method unblockJuniorCard" , idn);

        val object = new ColvirRequestWrapper(BankPackage.COLVIR_CARD_UNBLOCK.getDescription());
        val payload = new ColvirRequestPayload(idn, null, unblockReasonDscr,
                null, null,null, null, null);
        object.setPayload(payload);

        ColvirResponsePayload response = colvirService.getColvirRestTemplate(object, token); //запрос в Колвир
        log.info("Successful request to the system COLVIR, with idn: {}, method unblockJuniorCard" , idn);

        return new GenericResponse<>(response.getSuccess());
    }

    @Override
    public void getReissueCardStatusFromColvir (Reissue reissue) { //Метод проверки статуса перевыпуска для checkReissueStatusJob

        String token = colvirService.getAuthToken(BearerTokenType.COMMON); //запрос токена в keycloack
        log.info("Successful request a token for COLVIR, with idn: {}, method getReissueCardStatusFromColvir" , reissue.getOldIdn());

        val object = new ColvirRequestWrapper(BankPackage.COLVIR_CARD_REISSUE_STATUS.getDescription());
        val payload = new ColvirRequestPayload(null, null, null,
                String.valueOf(reissue.getId()), null, null, null, null);
        object.setPayload(payload);

        ColvirResponsePayload response = colvirService.getColvirRestTemplate(object, token); //запрос в Колвир на проверку статуса
        log.info("Successful request to the system COLVIR, with idn: {}, method getReissueCardStatusFromColvir" , reissue.getOldIdn());

        if (response.getSuccess()) {
            //сохранение записи в историю заявки перевыпуска
            pushReissueHistoryTable(reissue.getOldIdn(), StatusType.REISSUE_COMPLETE, null);

            reissue.setNewIdn(response.getNewCardIdn());
            reissue.setReissueStatus(StatusType.REISSUE_COMPLETE);
            reissue.setActive(false);

            //пуш уведомление junior
            bankRequestService.sendMessage(String.valueOf(reissue.getJuniorPhone()), reissue.getJuniorFullName(), UserType.JUNIOR,
                    PushSmsDescCode.SERVICE_NAME.getDescription(), getJuniorPushReissueText(String.valueOf(reissue.getJuniorPhone())),
                    PushSmsType.JUNIOR_PUSH);

            //пуш уведомление parent
            bankRequestService.sendMessage(String.valueOf(reissue.getParentPhone()), reissue.getJuniorFullName(), UserType.PARENT,
                    PushSmsDescCode.SERVICE_NAME.getDescription(), getParentPushReissueText(String.valueOf(reissue.getParentPhone())),
                    PushSmsType.PARENT_PUSH); //пуш уведомление parent
        } else if (Objects.equals(response.getCode(), "CARD_OPEN")) {
            reissue.setReissueStatus(StatusType.REISSUE_IN_PROGRESS);

            //сохранение записи в историю заявки перевыпуска
            pushReissueHistoryTable(reissue.getOldIdn(), StatusType.REISSUE_IN_PROGRESS, response.getMessage());
        } else if (Objects.equals(response.getCode(), "CLOSE_CARD_ERROR")) {
            //сохранение записи в историю заявки перевыпуска
            pushReissueHistoryTable(reissue.getOldIdn(), StatusType.CLOSE_CARD_ERROR, response.toString());

            reissue.setReissueStatus(StatusType.CLOSE_CARD_ERROR);
            reissue.setErrorMessage(response.toString());
            reissue.setActive(false);

            //разблокировка карты по ошибке перевыпуска
            unblockJuniorCard(reissue.getOldIdn(), MessageCode.CARD_REISSUE_ERROR.getDescription());
        } else {
            //сохранение записи в историю заявки перевыпуска
            pushReissueHistoryTable(reissue.getOldIdn(), StatusType.REISSUE_ERROR, response.toString());

            reissue.setErrorMessage(response.toString());
            reissue.setReissueStatus(StatusType.REISSUE_ERROR);
            reissue.setActive(false);
        }

        reissueRepository.save(reissue);
    }

    //Метод для блокировки и отправки на перевыпуск карты Junior
    @Override
    public GenericResponse<Boolean> reissueJuniorCard(BlockDto dto) {

        ColvirResponsePayloadV2 parent = juniorAuthService.getUserDataByPhone(
                juniorAuthService.getUserInfoRequest(dto.getParentPhone(), UserType.PARENT));
        //запрос на получения детей по номеру родителя

        if (ObjectUtils.isEmpty(parent) || CollectionUtils.isEmpty(parent.getChilds())) {
            return GenericResponse.error(MessageCode.PARENT_OR_CHILD_ENTITY_EMPTY);
        }

        for (ColvirResponsePayloadV2 child : parent.getChilds()) {
            if (child.getRbs().equals(dto.getIdn()) || child.getRbsList().contains(dto.getIdn())) { //проверка совпадения по IDN детей
                if (blockJuniorCardForReissue(dto, child, parent)) { //блокировка карты
                    val reissue = reissueRepository.findFirstByOldIdnAndActiveOrderByIdDesc(dto.getIdn(), true);
                    if (reissue.isPresent()) {
                        try {
                            Boolean reissueStart = reissueJuniorCardFunction(dto, reissue.get()); //отпрака карты на перевыпуск

                            if (reissueStart) {
                                //обновление статуса заявки в таблице перевыпуска
                                reissue.get().setReissueStatus(StatusType.REISSUE_START);
                                reissueRepository.save(reissue.get());
                            }
                            //сохранение записи в историю заявки перевыпуска
                            pushReissueHistoryTable(dto.getIdn(), StatusType.REISSUE_START, null);

                            return new GenericResponse<>(reissueStart);
                        } catch (Exception e) {
                            //сохранение записи в историю заявки перевыпуска
                            pushReissueHistoryTable(dto.getIdn(), StatusType.REISSUE_ERROR,
                                    MessageCode.CARD_REISSUE_INITIALIZING_ERROR.getDescription());

                            //закрытие очереди по причине ошибки перевыпуска карты и ведение аналитики ошибки
                            reissue.get().setErrorMessage(MessageCode.CARD_REISSUE_INITIALIZING_ERROR.getDescription());
                            reissue.get().setReissueStatus(StatusType.REISSUE_ERROR);
                            reissue.get().setActive(false);
                            reissueRepository.save(reissue.get());

                            //разблокировка карты в случае ошибки перевыпуска
                            unblockJuniorCard(dto.getIdn(), MessageCode.CARD_REISSUE_INITIALIZING_ERROR.getDescription());

                            return GenericResponse.error(MessageCode.COLVIR_REQUEST_ERROR);
                        }
                    }
                } else {
                    return GenericResponse.error(MessageCode.CARD_BLOCK_ERROR);
                }
            }
        }

        return GenericResponse.error(MessageCode.NO_MATCHING_CHILD_ERROR);
    }

    @Override
    public GenericResponse<Boolean> getReissueCardStatus (CheckReissueDto dto) { //Метод получения статуса перевыпуска

        val reissue = reissueRepository.findFirstByJuniorPhoneAndActiveOrderByIdDesc(Long.valueOf(dto.getJuniorPhone()), true);
        return reissue.map(value -> new GenericResponse<>(value.isActive())).orElseGet(() -> new GenericResponse<>(false));
    }

    //Метод генерации OTP кода
    @Override
    public GenericResponse<String> sendOtpFunction (SentOtpDto dto) {

        val validPhone = Utils.cutPhone(dto.getLogin());
        return new GenericResponse<>(bankRequestService.sendOtp(validPhone, dto.getLang()));
    }

    //Метод проверки OTP кода
    @Override
    public GenericResponse<Boolean> checkOtpFunction (CheckOtpDto dto) {

        return new GenericResponse<>(bankRequestService.checkOtp(dto.getRefId(), dto.getOtp()));
    }

    //Метод для передачи списка перевыпускаемых карт по номеру родителя
    @Override
    public GenericResponse<List<ReissueCardStatusDto>> getReissueJuniorCardStatusForBcc (ReissueCardStatusDto dto) {

        val reissueList = reissueRepository.findAllByParentPhoneAndActiveOrderByIdAsc(Long.valueOf(dto.getParentPhone()), true);

        if (reissueList != null && !reissueList.isEmpty()) {
            return new GenericResponse<>(reissueEntityListToDtoList(reissueList));
        } else {
            return new GenericResponse<>(reissueEntityListToDtoList(null));
        }
    }

    //Метод постановки в очередь на проверку статуса перевыпуска карты
    private void pushReissueTable (BlockDto dto, String juniorPhone, String juniorFullName, String parentFullName) {

        val reissueTable = new Reissue();
        reissueTable.setJuniorPhone(Long.valueOf(juniorPhone));
        reissueTable.setJuniorFullName(juniorFullName);
        reissueTable.setParentPhone(Long.valueOf(dto.getParentPhone()));
        reissueTable.setParentFullName(parentFullName);
        reissueTable.setOldIdn(dto.getIdn());
        reissueTable.setBlockReason(dto.getBlockReasonCode());
        reissueTable.setReissueStatus(StatusType.BLOCK_CARD);
        reissueRepository.save(reissueTable);

        //сохранение записи в историю заявки перевыпуска
        pushReissueHistoryTable(dto.getIdn(), StatusType.BLOCK_CARD, null);
    }

    //Метод реализации отправки запроса по блокировки и перевыпуска карты
    @Override
    public Boolean performColvirRequest(BlockDto dto, BankPackage colvirPackage, String method, Reissue reissue) {

        String token = colvirService.getAuthToken(BearerTokenType.COMMON); //запрос токена в keycloack
        log.info("Successful request a token for COLVIR, with idn: {}, method {}",
                dto.getIdn(), method);

        val object = new ColvirRequestWrapper(colvirPackage.getDescription());
        val payload = new ColvirRequestPayload(dto.getIdn(), dto.getBlockReasonCode(),
                null, colvirPackage == BankPackage.COLVIR_CARD_REISSUE ? String.valueOf(reissue.getId()) : null,
                MessageCode.REISSUE_CARD_CODE.getDescription(), null, null, null);
        object.setPayload(payload);

        ColvirResponsePayload response = colvirService.getColvirRestTemplate(object, token); //запрос в Колвир
        log.info("Successful request to the system COLVIR, with idn: {}, method {}", dto.getIdn(), method);

        return response.getSuccess();
    }

    private void pushReissueHistoryTable (String oldIdn, StatusType type, String result) {

        val reissue = reissueRepository.findFirstByOldIdnAndActiveOrderByIdDesc(oldIdn, true);

        if (reissue.isPresent()) {
            val reissueHistory = new ReissueHistory();
            reissueHistory.setOldIdn(oldIdn);
            reissueHistory.setReissueStatus(type);
            reissueHistory.setResultDescription(result);
            reissueHistory.setReissueId(reissue.get().getId());

            reissueHistoryRepository.save(reissueHistory);
        }
    }

    private List<ReissueCardStatusDto> reissueEntityListToDtoList(List<Reissue> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<ReissueCardStatusDto> list = new ArrayList<>( entityList.size() );
        for ( Reissue reissue : entityList ) {
            list.add( reissueEntityToDto( reissue ) );
        }

        return list;
    }

    private ReissueCardStatusDto reissueEntityToDto(Reissue entity) {
        if ( entity == null ) {
            return null;
        }

        ReissueCardStatusDto reissueDto = new ReissueCardStatusDto();

        reissueDto.setParentPhone(String.valueOf(entity.getParentPhone()));
        reissueDto.setJuniorPhone(String.valueOf(entity.getJuniorPhone()));
        reissueDto.setIdn(entity.getOldIdn());

        return reissueDto;
    }

    private String getParentPushReissueText(String juniorPhone) {
        MciResponsePayload parent = mciService.getParentLangByJuniorLogin(juniorPhone);
        return Utils.getPushText("PUSH_REISSUE_BCC", parent.getLang());
    }

    private String getJuniorPushReissueText(String juniorPhone) {
        MciResponsePayload junior = mciService.getJuniorLangByJuniorLogin(juniorPhone);
        return Utils.getPushText("PUSH_REISSUE_JB", junior.getLang());
    }

    //Метод блокировки карты для перевыпуска
    private Boolean blockJuniorCardForReissue(BlockDto dto, ColvirResponsePayloadV2 child, ColvirResponsePayloadV2 parent) {

        val reissue = reissueRepository.findFirstByOldIdnAndActiveOrderByIdDesc(dto.getIdn(), true);
        if (reissue.isPresent()) {
            throw new RequestErrorException(MessageCode.NAME_ALREADY_EXIST.getDescription());
        }

        Boolean block = performColvirRequest(dto, BankPackage.COLVIR_CARD_BLOCK, "blockJuniorCardForReissue", null);

        if (block) {
            log.info("The card was successfully blocked, with idn: {}", dto.getIdn());

            pushReissueTable(dto, child.getPhone(),
                    Utils.getFullName(child.getFirstName(), child.getLastName(), child.getMiddleName()),
                    Utils.getFullName(parent.getFirstName(), parent.getLastName(), parent.getMiddleName()));
            //сохранение карты в очередь для проверки статуса перевыпуска
        }

        return block;
    }

    private Boolean reissueJuniorCardFunction(BlockDto dto, Reissue reissue) { //Метод отправки карты в очередь перевыпуска
        return performColvirRequest(dto, BankPackage.COLVIR_CARD_REISSUE, "reissueJuniorCardFunction", reissue);
    }
}
