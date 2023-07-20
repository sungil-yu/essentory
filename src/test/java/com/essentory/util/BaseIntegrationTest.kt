package com.essentory.util

import com.essentory.service.VonageClientWrapper
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
abstract class BaseIntegrationTest: IntegrationTestConfiguration() {


}
