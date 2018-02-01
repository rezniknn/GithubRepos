package com.alexeyreznik.githubrepos.utils

import junit.framework.Assert.assertEquals
import okhttp3.Headers
import org.junit.Test

/**
 * Created by alexeyreznik on 31/01/2018.
 */
class LinkHeaderParserTest {

    @Test
    fun test_parseLinkHeader() {
        var headers = Headers.Builder()
                .add("Link", "<https://api.github.com/user/7378196/repos?page=2>; rel=\"next\", <https://api.github.com/user/7378196/repos?page=8>; rel=\"last\"")
                .build()
        var expectedTotalPages = 8


        var actualTotalPages = LinkHeaderParser.parseLinkHeader(headers)
        assertEquals(expectedTotalPages, actualTotalPages)


        headers = Headers.Builder()
                .add("Link", "<https://api.github.com/user/6583079/repos?page=1>; rel=\"prev\", <https://api.github.com/user/6583079/repos?page=1>; rel=\"last\", <https://api.github.com/user/6583079/repos?page=1>; rel=\"first\"")
                .build()
        expectedTotalPages = 1


        actualTotalPages = LinkHeaderParser.parseLinkHeader(headers)
        assertEquals(expectedTotalPages, actualTotalPages)


        headers = Headers.Builder()
                .add("Link", "Link: <https://api.github.com/user/7378196/repos?page=1>; rel=\"prev\", <https://api.github.com/user/7378196/repos?page=3>; rel=\"next\", <https://api.github.com/user/7378196/repos?page=8>; rel=\"last\", <https://api.github.com/user/7378196/repos?page=1>; rel=\"first\"")
                .build()
        expectedTotalPages = 82


        actualTotalPages = LinkHeaderParser.parseLinkHeader(headers)
        assertEquals(expectedTotalPages, actualTotalPages)
    }
}