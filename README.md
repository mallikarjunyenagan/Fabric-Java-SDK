

balance transfer java

Create and Deploy a Blockchain Network using Hyperledger Fabric SDK Java

This pattern will provision a Hyperledger Fabric 1.4.1 network consisting of two organizations, each maintaining two peer node, two certificate authorities (ca) for each organization and a solo ordering service. The following aspects will be demonstrated in this code pattern:

Create and initialize channel
Install and instantiate chain code
Register and enroll the users
Perform invoke and query on the blockchain network.
Note: This code pattern builds a Hyperledger Fabric 1.4.1 network and uses Hyperledger Fabric SDK java 1.4.1 .


1) To start the network

Open terminal in project folder and copy following commands

cd network
chmod +x build.sh
./build.sh


2) To install maven dependencies

cd ..

mvn install


3) Open the project in  eclipse and run the project 

4) Open postman and use the following apis provided below

a) To create channel
Type: POST
Url: http://localhost:9090/api/createChannel

b) To deploy chaincode
Type: POST
Url: http://localhost:9090/api/deployChaincode

c) To register user
Type:POST
Url: http://localhost:9090/api/registerUser

d) To invoke chaincode
Type: POST
Url: http://localhost:9090/api/invokeChaincode
Body: {
	"name":"suzuki",
	"make":"maruti",
	"model":"eight",
	"colour":"black",
	"owner":"vivek"
      }

e) To Query chaincode
Type: GET
Url: http://localhost:9090/api/queryChaincode?name=suzuki
Url2: http://localhost:9090/api/GetAllRecords
f) To Upload File
Type: POST
Url: http://localhost:9090/api/uploadFile
Body->FormData->key:file Dropdown:File Value:Choose Files(Choose an Image)

g) To Download File
Type: GET
Url: http://localhost:9090/api/downloadFile?hash=QmbQiRBGafSkLgd4XHd9p2QvUJb4S2Ptzi7S5XCKhbb9eu

References:

https://github.com/IBM/blockchain-application-using-fabric-java-sdk

Video Tutorial :

https://www.youtube.com/watch?v=vCTabgkvfS0&feature=youtu.be
