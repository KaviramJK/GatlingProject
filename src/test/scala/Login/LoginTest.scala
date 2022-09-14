package Login

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class LoginTest extends Simulation{

  val httpProtocol=http.baseUrl("http://localhost:3000/")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
// create own data
  //store that data to feeders/json file
  //retrive to login test/deletion

  def Register(): ChainBuilder ={
    exec(http("Registration")
    .post("api/Users/").body(StringBody("{\n  \"email\": \"raam50@gmail.com\",\n  \"password\": \"Test@123\",\n  \"passwordRepeat\": \"Test@123\",\n  \"securityQuestion\": {\n    \"id\": 7,\n    \"question\": \"Name of your favorite pet?\",\n    \"createdAt\": \"2022-09-05T04:11:12.495Z\",\n    \"updatedAt\": \"2022-09-05T04:11:12.495Z\"\n  },\n  \"securityAnswer\": \"Catt\"\n}"))
    .check(status.is(201)))

  }


  def login() = {
   // var token=""
    exec(http("login")
      .post("rest/user/login").body(StringBody("{\n\"email\": \"raam50@gmail.com\",\n\"password\": \"Test@123\"\n}"))
      .check(status.is(200))
      .check(jsonPath("$.authentication.token").saveAs("jobToken"))
    .check(jsonPath("$.authentication.bid").saveAs("bid"))
      .check(bodyString.saveAs("responseBody")))
      .exec{session => println(session("responseBody").as[String]);session}
      .exec{session=> println(session("jobToken").as[String]);session}
    //return token
  }




  def addToCart(): ChainBuilder = {
    exec(http("add to cart")
      .post("api/BasketItems/")
      .header("Authorization", "Bearer ${jobToken}")
      .body(StringBody("{\n  \"ProductId\": 3,\n  \"BasketId\": \"${bid}\",\n  \"quantity\": 1\n}")).asJson
     // .body(StringBody("{\n  \"ProductId\": 1,\n  \"BasketId\": \"${bid}\",\n  \"quantity\": 5\n}")).asJson
      .check(status.is(200))
    .check(jsonPath("$.data.id").saveAs("cartID")))
  }


  def deleteItemCart(): ChainBuilder ={
    exec(http("Delete Item from Cart")
    .delete("api/BasketItems/${cartID}")
    .check(status.is(200)))
  }


  val scn=scenario("Juice shop Login")
    .exec(Register())
    .pause(3)
      .exec(login())
      .pause(5)
      .exec{session=> println(session("jobToken").as[String]);session}
    .pause(3)
      .exec(addToCart())
    .exec(deleteItemCart())






  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)


}
