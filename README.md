# Order Processing services
This is the solution for the technical task from media-market-saturn group
 
 ## Architecture
 ![Alt text](docs/SystemArch.jpg?raw=true)

####Adopting this architecture gives us the below benefits:
- Scalability --> it is easy to scale any of the system components alone based on business needs.
- Resiliency ---> events & commands are saved on time order format (kafka, mailbox) so system can restore processing 
after failures hence eventual consistency is guaranteed.
- Use of Akka echo-system provide us maximum performance of our hardware.  
 ## Tech Stack
 - MicroService architecture adopting event sourcing.
 - Java 11
 - Spring boot
 - Akka actor system (used to implement Commands/ events adopted in event source{CQRS} patterns)
 - Akka http server/client (for reactive non-blocking rest end points)
 - Kafka messaging (event store to be replace by akka Persistence)
 - JPA/Hibernate + H2 DB (testing) or MySQl for development
 - Gradle as build tool
 - docker and docker compose for building container env to run the services


## Build
 The system is using Gradle as build tool so either to build each microservice individually or use 
 "build.sh" file that I have created for easy use.
 
 ./build.sh ------> compile and build java jars
 
 docker-compose build ---> to build docker images for the tow microservices 
 
## Run
To run all microservices at once please use the below

docker-compose up -d --build 


## Further Improvements 

This is just a draft version and below improvements can be done if given more time:

- Replace Kafka as CQRS event store with Akka persistence.
- Use akka typed actors instead of untyped actors.
- Replace akka actor custom mailbox storage with Akka persistence.
- Add more test cases to cover more test scenarios.
- Replace docker-compose with kubernetes. 

## End Points

### Create new order endpoint (POST)
URL --> http://{API_GATEWAY_HOST}:{PORT}/api/v1/order-service/order

Request:
{
    "createdByUserId": 1,
    "orderItems": [
        {
            "quantity": 2,
            "skuId": 1,
            "unitPrice": 15
        },
        {
            "quantity": 1,
            "skuId": 2,
            "unitPrice": 10
        }
    ],
    "paymentInfo": {
        "discountAmount": 0,
        "paymentAmount": 30,
        "paymentMethod": "CARD",
        "voucherCode": null
    },
    "shippingInfo": {
        "address": "address",
        "city": "Munich",
        "contactPhone": "01234567",
        "postCode": "80992"
    }
}

Response:
  {
      "orderId": 3,
      "redirectURL": "http://payment-service:8080/payment/"
  }
  

### Update order status (PUT)
URL --> http://{API_GATEWAY_HOST}:{PORT}/api/v1/order-service/order/{ORDER_ID}?status=paid


###Get order details (GET)
URL --> http://{API_GATEWAY_HOST}:{PORT}/api/v1/order-service/order/{ORDER_ID}

###Get order payment details (GET)
URL --> http://{API_GATEWAY_HOST}:{PORT}/api/v1/order-service/order/{ORDER_ID}/paymentInfo

###Get order shipping details (GET)
URL --> http://{API_GATEWAY_HOST}:{PORT}/api/v1/order-service/order/{ORDER_ID}/shippingInfo

