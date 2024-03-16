
# 설명

다음 사이트 참조
<a href='https://macaronics.net/m01/spring/view/2183' target='_blank'>macaronics.net </a>



 ## 실행방법 
 #### 1.redis 설치
 #### 2.backend 디렉토리의 스프링부트 구동
 #### 3.frontend 디렉토리 에서 npm i &&  npm run start


## 1. redis 설치
```

 

0. 우분트22 - Docker 설치방법
https://haengsin.tistory.com/128

1. 도커 redis  데이터 외부 저장경로 생성
$ mkdir /docker
$ mkdir /docker/redis/
$ mkdir /docker/redis/data



2. 도커 redis 설치 및 실행
#1)윈도우
docker run -v c:/docker/redis/data:/data --name my-redis -p 6379:6379 redis redis-server --appendonly yes --requirepass test1234

#2)리눅스
docker run -v /docker/redis/data:/data --name my-redis -p 6379:6379 redis redis-server --appendonly yes --requirepass test1234
#docker inspect [container-name] 명령어로 확인하면, 볼륨 마운트 설정확인
$ docker inspect my-redis



3. container 접속 및 테스트
$ docker exec -it my-redis redis-cli
#비밀번호 인증 안된 상태에서 redis 명령어 "keys * "를 실해하면 NOAUTH 에러 발생 한다.

# 비밀번호 인증
$ auth test1234

# 정상작동 확인
$ set a bbbb
$ get a



4.컨테이너를 중지후 재시작  데이터 보존되는 것을 확인
$ docker stop my-redis
$ docker start my-redis

$ docker exec -it my-redis redis-cli
$ auth test1234
$ get a



5.비밀번호 업데이트 ==> 그러나 도커를 정지하고 재구동 하면 원래비밀번호로 변경된다. 
따라서 컨테이너 삭제후  docker run 을 통해 다시 컨테이너를 실행해야 한다.

127.0.0.1:6379> config set requirepass 1234
종료후 재 접속 확인
$exit
$ docker exec -it my-redis redis-cli
$ auth 1234
$ get a

```


# 리액트에서 인스톨시 [Error] npm install 후 reify~ 프리징 오류가 날경우
다음과 같이 설치한다.
```
$ npm install --verbose

```
##### 참조 : <a href="https://stackoverflow.com/questions/16873973/npm-install-hangs"> https://stackoverflow.com/questions/16873973/npm-install-hangs </a>






