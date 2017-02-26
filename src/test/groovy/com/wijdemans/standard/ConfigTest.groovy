package com.wijdemans.standard

class ConfigTest extends spock.lang.Specification {

    def "when i pass in a valid groovy file i expect all values to be readable"() {
        given:
        System.properties[Config.ENV_PROPS_LOCATION] = "./src/test/groovy/com/wijdemans/standard"
        new Config().init()

        expect:
        Config.get("hello") == "world"
        Config.get("security.jwt.signedKey") == "mykey"
    }
}
