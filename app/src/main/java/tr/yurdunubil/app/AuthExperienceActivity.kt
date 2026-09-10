package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

private val ABg=Color(0xFF041712); private val AGreen=Color(0xFF35E7A1); private val AText=Color(0xFFF5FAF7); private val AMuted=Color(0xFFB8C9C2); private val ACard=Color(0xFF0B211B)

class AuthExperienceActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState); runCatching{SupabaseClientProvider.client.handleDeeplinks(intent)}; val recovery=intent.dataString?.contains("recovery",true)==true; setContent{AuthScreen(intent.getBooleanExtra("register",false),recovery,{openMain()},{finish()})}}
    private fun openMain(){startActivity(Intent(this,RetentionMainActivity::class.java).apply{flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK})}
}

@Composable private fun AuthScreen(initialRegister:Boolean,recovery:Boolean,onDone:()->Unit,onBack:()->Unit){
    var register by remember{mutableStateOf(initialRegister)}; var forgot by remember{mutableStateOf(false)}; var email by remember{mutableStateOf("")}; var password by remember{mutableStateOf("")}; var newPassword by remember{mutableStateOf("")}; var confirm by remember{mutableStateOf("")}; var show by remember{mutableStateOf(false)}; var busy by remember{mutableStateOf(false)}; var ok by remember{mutableStateOf<String?>(null)}; var error by remember{mutableStateOf<String?>(null)}; val scope=rememberCoroutineScope()
    fun clear(){ok=null;error=null}
    fun action(block:suspend()->Unit,success:()->Unit){scope.launch{busy=true;clear();runCatching{block()}.onSuccess{success()}.onFailure{error=authMessage(it)};busy=false}}
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(ABg,Color(0xFF0A3029),ABg)))){
        LazyColumn(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
            item{Row(verticalAlignment=Alignment.CenterVertically){if(forgot||recovery)IconButton(onClick={if(recovery)onBack()else{forgot=false;clear()}}){Icon(Icons.Default.ArrowBack,"Geri",tint=AText)}else Spacer(Modifier.width(48.dp));Column(Modifier.weight(1f)){Text("Yurdunu Bil",color=AText,fontSize=28.sp,fontWeight=FontWeight.Black);Text("KPSS • Türkiye Coğrafyası",color=AGreen,fontSize=11.sp)};Icon(Icons.Default.Map,null,tint=AGreen)}}
            item{Card(shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(containerColor=ACard)){Column(Modifier.fillMaxWidth().padding(18.dp)){
                if(recovery){
                    Text("Yeni şifreni belirle",color=AText,fontSize=24.sp,fontWeight=FontWeight.Black);Text("Hesabın için yeni bir şifre oluştur.",color=AMuted,fontSize=12.sp);Spacer(Modifier.height(16.dp));PasswordField("Yeni şifre",newPassword,{newPassword=it;clear()},show){show=!show};Spacer(Modifier.height(10.dp));PasswordField("Yeni şifre tekrar",confirm,{confirm=it;clear()},show){show=!show};Spacer(Modifier.height(14.dp));ActionButton("Şifreyi Güncelle",busy){action({require(newPassword.length>=6){"Şifre en az 6 karakter olmalı."};require(newPassword==confirm){"Şifreler aynı olmalı."};SupabaseClientProvider.client.auth.updateUser{password=newPassword}}){ok="Şifren güncellendi. Yeni şifrenle giriş yapabilirsin."}}
                }else if(forgot){
                    Text("Şifremi unuttum",color=AText,fontSize=24.sp,fontWeight=FontWeight.Black);Text("E-posta adresine şifre yenileme bağlantısı gönderelim.",color=AMuted,fontSize=12.sp);Spacer(Modifier.height(16.dp));EmailField(email){email=it;clear()};Spacer(Modifier.height(14.dp));ActionButton("Şifre Yenileme Bağlantısı Gönder",busy){action({require(email.contains("@")){"Geçerli bir e-posta adresi gir."};SupabaseClientProvider.client.auth.resetPasswordForEmail(email=email.trim(),redirectUrl="yurdunubil://auth")}){ok="Şifre yenileme bağlantısı gönderildi. Gelen kutunu ve spam klasörünü kontrol et."}}
                }else{
                    Row(Modifier.fillMaxWidth().background(Color(0xFF17372D),RoundedCornerShape(15.dp)).padding(4.dp)){TabButton("Giriş Yap",!register,Modifier.weight(1f)){register=false;clear()};TabButton("Yeni Hesap",register,Modifier.weight(1f)){register=true;clear()}}
                    Spacer(Modifier.height(16.dp));Text(if(register)"KPSS yolculuğuna katıl." else "Kaldığın yerden devam et.",color=AText,fontSize=25.sp,fontWeight=FontWeight.Black);Spacer(Modifier.height(12.dp));EmailField(email){email=it;clear()};Spacer(Modifier.height(10.dp));PasswordField("Şifre",password,{password=it;clear()},show){show=!show}
                    if(!register)TextButton(onClick={forgot=true;clear()},modifier=Modifier.align(Alignment.End)){Text("Şifremi unuttum",color=AGreen,fontSize=12.sp,fontWeight=FontWeight.Bold)}
                    Spacer(Modifier.height(4.dp));ActionButton(if(register)"Hesap Oluştur" else "Giriş Yap",busy){action({require(email.contains("@")){"Geçerli bir e-posta adresi gir."};require(password.length>=6){"Şifre en az 6 karakter olmalı."};if(register)SupabaseClientProvider.client.auth.signUpWith(Email){this.email=email.trim();this.password=password}else SupabaseClientProvider.client.auth.signInWith(Email){this.email=email.trim();this.password=password}}){if(register&&SupabaseClientProvider.client.auth.currentSessionOrNull()==null)ok="Hesabın oluşturuldu. E-posta kutundaki doğrulama bağlantısını kontrol et." else onDone()}}
                }
                if(ok!=null){Spacer(Modifier.height(10.dp));Text(ok!!,color=AGreen,fontSize=11.sp);if(recovery)TextButton(onClick=onDone){Text("Uygulamaya devam et",color=AGreen,fontWeight=FontWeight.Bold)}}
                if(error!=null){Spacer(Modifier.height(10.dp));Text(error!!,color=Color(0xFFFFB5AD),fontSize=11.sp)}
            }}}
        }
    }
}

@Composable private fun EmailField(value:String,onChange:(String)->Unit)=OutlinedTextField(value=value,onValueChange=onChange,modifier=Modifier.fillMaxWidth(),label={Text("E-posta")},leadingIcon={Icon(Icons.Default.Email,null)},singleLine=true)
@Composable private fun PasswordField(label:String,value:String,onChange:(String)->Unit,visible:Boolean,toggle:()->Unit)=OutlinedTextField(value=value,onValueChange=onChange,modifier=Modifier.fillMaxWidth(),label={Text(label)},leadingIcon={Icon(Icons.Default.Lock,null)},trailingIcon={IconButton(onClick=toggle){Icon(if(visible)Icons.Default.VisibilityOff else Icons.Default.Visibility,null)}},singleLine=true,visualTransformation=if(visible)VisualTransformation.None else PasswordVisualTransformation())
@Composable private fun ActionButton(text:String,busy:Boolean,onClick:()->Unit)=Button(onClick=onClick,enabled=!busy,modifier=Modifier.fillMaxWidth().height(54.dp),shape=RoundedCornerShape(16.dp),colors=ButtonDefaults.buttonColors(containerColor=AGreen,contentColor=Color(0xFF06251B))){if(busy)CircularProgressIndicator(Modifier.size(21.dp),color=Color(0xFF06251B),strokeWidth=2.dp)else Text(text,fontWeight=FontWeight.Black)}
@Composable private fun TabButton(text:String,selected:Boolean,modifier:Modifier,onClick:()->Unit)=Box(modifier.clip(RoundedCornerShape(13.dp)).background(if(selected)Color.White.copy(alpha=.12f)else Color.Transparent).clickable(onClick=onClick).padding(vertical=11.dp),contentAlignment=Alignment.Center){Text(text,color=if(selected)AText else AMuted,fontSize=13.sp,fontWeight=if(selected)FontWeight.Bold else FontWeight.Medium)}
private fun authMessage(e:Throwable):String{val r=(e.message?:"").lowercase();return when{ "already registered"in r||"user already registered"in r->"Bu e-posta ile zaten kayıt olunmuş. Giriş Yap sekmesine geçip giriş yapmayı dene.";"invalid_credentials"in r||"invalid login credentials"in r->"E-posta veya şifre hatalı. Şifreni unuttuysan Şifremi Unuttum seçeneğini kullanabilirsin.";"email not confirmed"in r->"E-posta adresini doğruladıktan sonra tekrar giriş yap.";"network"in r||"timeout"in r||"unable to resolve host"in r->"Bağlantı kurulamadı. İnternetini kontrol edip tekrar dene.";else->"İşlem tamamlanamadı. Bilgilerini kontrol edip tekrar dene."}}
