docker-compose up --build

docker exec -it cpabe_org0-cpabe_1 /bin/sh
docker exec -it cpabe_org1-cpabe_1 /bin/sh
docker exec -it cpabe_org2-cpabe_1 /bin/sh

java -cp target/dependency/*:target/org1-cpabe-0.0.1-SNAPSHOT.jar com.example.Main
java -cp target/dependency/*:target/org2-cpabe-0.0.1-SNAPSHOT.jar com.example.Main

GET_PUB_MSK|org1                                                     //获取公钥和主密钥
GET_PRIKEY|org1|org:org1 dept:dept1 level:1      //获取私钥
EncFile:|file1.txt                                                             //加密文件
DecFile:|encrypted_file1.txt                                        //解密文件

GET_PUB_MSK|org2
GET_PRIKEY|org2|org:org1 dept:dept2 level:2
EncFile:|file2.txt
DecFile:|encrypted_file2.txt

cd src/main/java/com/example/    keys|files|encrypted|decrypted

cat encrypted_file1.txt
cat decrypted_file2.txt

/home/jjh/Desktop/cpabe
ssh jjh@222.20.126.154
cd  /cluster/cpabe
scp filename username@ip_address:/home/username
scp -r source_dir username@ip_address:/home/username/target_dir
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <容器名称或ID>

