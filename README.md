# gitlab_MR_notification

gitlab 의 그룹에 열린 상태인 MR 정보를 Slack 메시지로 전송하는 어플리케이션입니다.

## 메시지 예
``` 
MR_TITLE : 생성일 2022-09-28 (mr_url) / Asignee, Reviewers...

MR_TITLE : 생성일 2022-09-28 (mr_url) / Asignee, Reviewers...

...
```

## 사용법
### docker-compose
```yml
version: "3"
services:
  mr-notification:
    image: sinna94/mr-notification
    environment:
      GITLAB_TOKEN: ${gitlab_access_token}
      GITLAB_GROUP_ID: ${gitlab_group_id}
      SLACK_TOKEN: ${slack_bot_TOKEN}
      SLACK_CHANNEL_ID: ${slack_channel_id}
```