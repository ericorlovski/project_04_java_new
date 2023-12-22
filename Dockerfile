FROM openjdk:17
ARG PROFILE=docker
ENV PROFILE_ENV=$PROFILE
RUN mkdir -p /opt/java/services/dbp-juniorbank-account-manage-service/ && mkdir -p /var/log/omni/services/ && chmod 777 /var/log/omni/services
COPY build/libs/dbp-juniorbank-account-manage-service-0.0.1.jar /opt/java/services/dbp-juniorbank-account-manage-service/accountManageService.jar
WORKDIR /opt/java/services/dbp-juniorbank-account-manage-service/
ENTRYPOINT java -Dspring.profiles.active=$PROFILE_ENV -jar accountManageService.jar
