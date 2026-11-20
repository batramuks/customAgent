This is a custom agent genrated code using AI agent through github co-pilot

Put your agent file on following location:

C:\git-code\DEMAND-CCD2\CustomAgent\.github\agents

Agent file name:

spring-boot-developer.agent.md file


1 Agent will create a plan of impelmentation
2.Ask for your approval
3.Once you approved it will do the following
	write code
	write Junit
	Run the Junit
	Build the code.

PROMPT: 2nd Prompt (Tested)	

create a endpoint GET /applicaitions/{applicationId} 
applicationId as path param

Application Table

Name                         Null?    Type         
---------------------------- -------- ------------ 
APP_SEQ_ID                   NOT NULL NUMBER(38)   
APP_ID                       NOT NULL VARCHAR2(40) 
APP_START_TIME                        TIMESTAMP(6) 
APP_END_TIME                          TIMESTAMP(6) 
APP_APPLICATION_STATUS       NOT NULL VARCHAR2(40) 
APP_PROCESS_STATE            NOT NULL VARCHAR2(40) 
APP_CMND_PROCESS_DEF_NAME             VARCHAR2(40) 
APP_CMND_PROCESS_DEF_VERSION          VARCHAR2(40) 
APP_API_VERSION                       VARCHAR2(40) 
APP_COUNTRY_CODE             NOT NULL VARCHAR2(2)  
APP_CURRENCY_CODE            NOT NULL VARCHAR2(3)  
APP_CREATED_BY               NOT NULL VARCHAR2(40) 
APP_CREATED_ON                        TIMESTAMP(6) 
APP_MODIFIED_BY              NOT NULL VARCHAR2(40) 
APP_MODIFIED_ON                       TIMESTAMP(6) 
APP_LOCK                     NOT NULL NUMBER(38)   
APP_CSP_TIMESTAMP                     TIMESTAMP(6) 
APP_MANUAL_SCORING_NEEDED             NUMBER(38)   