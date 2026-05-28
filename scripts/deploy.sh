#!/bin/bash

# 1. 로그 파일 경로 변수로 잡기
LOG_FILE=/home/ec2-user/spring/logs/app-$(TZ=Asia/Seoul date +%Y%m%d).log

# 2. logs 폴더 생성
mkdir -p /home/ec2-user/spring/logs

# 3. 배포 시작 시간 기록
echo "===== 배포 시작: $(TZ=Asia/Seoul date '+%Y-%m-%d %H:%M:%S') =====" >> $LOG_FILE

# 4. 기존 프로세스 있다면 종료
if pgrep -f '*.jar' > /dev/null; then
    kill -9 $(pgrep -f '*.jar')
fi

# 5. Spring 프로세스 기동
nohup java -jar /home/ec2-user/spring/*.jar >> $LOG_FILE 2>&1 &