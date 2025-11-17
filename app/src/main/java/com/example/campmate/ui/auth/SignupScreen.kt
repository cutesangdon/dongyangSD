package com.example.campmate.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// 메인 컨테이너: 4단계 화면 전환을 관리합니다.
@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val signupState by viewModel.signupState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(signupState) {
        when (val state = signupState) {
            is SignupUiState.Success -> {
                Toast.makeText(context, "회원가입 성공! 로그인 해주세요.", Toast.LENGTH_SHORT).show()
                onSignupSuccess()
            }
            is SignupUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    NavHost(navController = navController, startDestination = "step1") {
        composable("step1") { SignupStep1(navController, viewModel) }
        composable("step2") { SignupStep2(navController, viewModel) }
        composable("step3") { SignupStep3(navController, viewModel) }
        composable("step4") { SignupStep4(viewModel) }
    }
}

// --- 각 단계별 화면 ---

@Composable
fun SignupStep1(navController: NavController, viewModel: SignupViewModel) {
    val customerId by viewModel.customerId.collectAsState()
    val password by viewModel.password.collectAsState()
    val email by viewModel.email.collectAsState()
    val name by viewModel.name.collectAsState()
    //25.11.16 KM 추가
    val phone by viewModel.phone.collectAsState()


    SignupPageLayout(title = "기본 정보 입력 (1/4)", onNext = { navController.navigate("step2") }) {
        OutlinedTextField(value = customerId, onValueChange = viewModel::onCustomerIdChange, label = { Text("아이디") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = viewModel::onPasswordChange, label = { Text("비밀번호") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = viewModel::onEmailChange, label = { Text("이메일") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = name, onValueChange = viewModel::onNameChange, label = { Text("닉네임") }, modifier = Modifier.fillMaxWidth())

        //25.11.16 KM 추가
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = viewModel::onPhoneChange, // 25.11.16 KM 수정
            label = { Text("전화번호 (예: 01012345678)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

    }
}

@Composable
fun SignupStep2(navController: NavController, viewModel: SignupViewModel) {
    val options = listOf("오토캠핑", "백패킹", "글램핑", "카라반")
    val selectedOption by viewModel.style.collectAsState()

    SignupPageLayout(title = "선호하는 캠핑 스타일 (2/4)", onNext = { navController.navigate("step3") }) {
        SingleChoiceSelector(options = options, selectedOption = selectedOption, onOptionSelected = viewModel::onStyleChange)
    }
}

@Composable
fun SignupStep3(navController: NavController, viewModel: SignupViewModel) {
    val options = listOf("산속", "바다", "계곡", "호수")
    val selectedOption by viewModel.background.collectAsState()

    SignupPageLayout(title = "선호하는 캠핑 환경 (3/4)", onNext = { navController.navigate("step4") }) {
        SingleChoiceSelector(options = options, selectedOption = selectedOption, onOptionSelected = viewModel::onBackgroundChange)
    }
}

@Composable
fun SignupStep4(viewModel: SignupViewModel) {
    val options = listOf("솔로", "커플", "친구", "가족", "반려견")
    val selectedOption by viewModel.type.collectAsState()
    val signupState by viewModel.signupState.collectAsState()

    SignupPageLayout(title = "주로 누구와 함께하나요? (4/4)", onNext = { viewModel.signup() }, nextButtonText = "회원가입 완료") {
        SingleChoiceSelector(options = options, selectedOption = selectedOption, onOptionSelected = viewModel::onTypeChange)
        if (signupState is SignupUiState.Loading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

// --- 공용 UI 컴포넌트 ---

@Composable
fun SignupPageLayout(
    title: String,
    onNext: () -> Unit,
    nextButtonText: String = "다음",
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        content()
        Spacer(Modifier.weight(1f))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Text(nextButtonText)
        }
    }
}

@Composable
fun SingleChoiceSelector(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        options.forEach { option ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (option == selectedOption),
                        onClick = { onOptionSelected(option) }
                    )
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == selectedOption),
                    onClick = { onOptionSelected(option) }
                )
                Spacer(Modifier.width(16.dp))
                Text(text = option, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
