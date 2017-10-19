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

    def "when i read a non existing value using a default fallback i expect the fallback"() {
        when:
        def v = Config.get("not", "default")
        then:
        v == "default"
    }

    def "when i read a non existing value with no fallback i expect the an exception"() {
        when:
        Config.get("not-there")

        then:
        thrown(IllegalArgumentException.class)
    }

    def "when i read a non existing int value using a default fallback i expect the fallback"() {
        when:
        def v = Config.getInt("not", 10)
        then:
        v == 10
    }

    def "when i read a non existing int value with no fallback i expect the an exception"() {
        when:
        Config.getInt("not-there")

        then:
        thrown(IllegalArgumentException.class)
    }

    def "when i read custom properties i expect to read that file from some base location"() {
        readPropertiesFrom
    }
}