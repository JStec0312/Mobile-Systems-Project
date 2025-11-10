package com.example.petcare.integration.data.user_repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.petcare.data.repository.UserRepository
import com.example.petcare.domain.repository.IUserRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest

class CreateUserTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @Inject
    lateinit var userRepository: IUserRepository
    @Inject
    lateinit var fireStore: FirebaseStorage
    @Before
    fun init(){
        hiltRule.inject()
    }
    @Test
     fun test() = runTest{
        val email = "test@gmail.com"
        val password = "123456"
        val displayName = "testUser123"
        userRepository.createUser(email, password, displayName)
    }

}