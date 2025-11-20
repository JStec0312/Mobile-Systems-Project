package com.example.petcare.presentation.sign_in

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.petcare.presentation.common.BaseScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petcare.R
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.petcare.presentation.common.PetTextField

@Composable
fun SignInRoute(
    viewModel: SignInViewModel = hiltViewModel(),
    onNavigateToSignUp: () -> Unit,
    onNavigateToMyPets: () -> Unit
){
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    if(state.isSuccessful) {
        LaunchedEffect(key1 = Unit) {
            onNavigateToMyPets()
        }
    }

    if(state.error != null) {
        LaunchedEffect(key1=state.error) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        }
    }

    SignInScreen(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSubmit = viewModel::onSubmit,
        onNavigateToSignUp = onNavigateToSignUp
    )
}

@Composable
fun SignInScreen(
    state: SignInState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {

    BaseScreen {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.paw_prints),
                contentDescription = "",
                modifier = Modifier
                    .scale(scaleX = -1f, scaleY = -1f)
                    .align(Alignment.TopStart)
                    .offset(x = 120.dp, y  = 150.dp)
                    .size(500.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.petcare_logo_purple),
                    contentDescription = "Petcare logo",
                    modifier = Modifier
                        .size(220.dp)
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "SIGN IN",
                    color = Color(0xFF605397),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.offset(y = (-30).dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                PetTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = "Email",
                    keyboardType = KeyboardType.Text
                )
                Spacer(modifier = Modifier.height(26.dp))
                PetTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = "Password",
                    keyboardType = KeyboardType.Text,
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(38.dp))
                Button(
                    onClick = onSubmit,
                    modifier = Modifier
                        .height(76.dp)
                        .width(300.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    if(state.isLoading){
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "LOGIN",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "No account yet?",
                        color = Color(0xFF605397),
                        fontSize = 16.sp,
                    )
                    TextButton(onClick = onNavigateToSignUp) {
                        Text(
                            text = "Sign up",
                            color = Color(0xFF605397),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(
        state = SignInState(),
        onEmailChange = {},
        onPasswordChange = {},
        onSubmit = {},
        onNavigateToSignUp = {}
    )
}