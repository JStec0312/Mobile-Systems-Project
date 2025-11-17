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
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest

class CreateUserTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @Inject
    lateinit var userRepository: IUserRepository
    @Inject
    lateinit var fireStorage: FirebaseStorage

    @Inject
    lateinit var db: FirebaseFirestore

    @Before
    fun init(){
        hiltRule.inject()
    }
    @Test
     fun test() = runTest{
         Log.d("test","Firestore integration test")
        Log.d("Storage", fireStorage.toString())
        val email = "test@gmail.com"
        val password = "123456"
        val displayName = "testUser123"
        val user = userRepository.createUser(email, password, displayName)
        val docRef = db.collection("users").document(user.id)
        val result = docRef.get().await()
        assert(result.exists())
        Log.d("test_result", result.toString())

    }

}