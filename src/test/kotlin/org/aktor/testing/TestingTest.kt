package org.aktor.testing

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource




class TestingTest {

    @Test  @Disabled  //they are all failing on purpose
    fun assertEqualTest()  {
        assertAll(
                { 1 eq 2 },
                { 3 `==` 4 },
                { 5 `≡` 6 },
                { 7 同 8 }
        )
    }

    @Test @Disabled  //they are all failing on purpose
    fun assertNotEqualTest() {
        1 eq 2
    }

    @Test @Disabled  //they are all failing on purpose
    fun assertWrongTypeTest() {
        1 eq "z"
    }



    @ParameterizedTest()
    @CsvSource(
            "4, 16",
            "2, 4",
            "9, 81")
    fun assertParametricTest(a: Int, b: Int) {

        a*a eq b
    }


    @ParameterizedTest()
    @TabularSource(
            "4 | 16",
            "2 | 4",
            "9 | 81")
    fun assertTabParametricTest(a: Int, b: Int) {

        a*a eq b
    }

    @ParameterizedTest()
    @TabularSource(
            "a")
    fun assertTabParametricTestString1(a: String) {

        a eq "a"
    }

    @ParameterizedTest()
    @TabularSource(
            "7")
    fun assertTabParametricTestInt1(a: Int) {

        a eq 7
    }

    @ParameterizedTest()
    @TabularSource(
            "7 | 8")
    fun assertTabParametricTestInt2(a: Int, b: Int) {

        a eq (b-1)
    }

    @ParameterizedTest()
    @TabularSource(
            "7 | 8 | 15",
            "1 | 2 | 3",
            "9 | 8 | 15")
    fun assertTabParametricTestInt3(a: Int, b: Int, c: Int) {

        (a+b) eq c
    }

    @ParameterizedTest()
    @TabularSource(
            "6", "8", "10")
    fun assertTabParametricTestIntML(a: Int) {

        (a % 2) eq 0
    }

    /*
    Spock test example:

    def 'basic math'() {

       given:
       def i = 1

       when:
       i+= 1

       then:
       i == 2

       //this given+when+then is a stupid example
       //which could also be written as
       //expect: 1+1 == 2
    }


    How to write a kotlin DSL like:
    fun 'basic math' =
        given {
           val i = 1 // how to pass this to when?
        }
        when {
           i + 1 // returned by lambda and passed to 'then' as result
        }
        then { result ->
          result eq 2
        }

    */
}