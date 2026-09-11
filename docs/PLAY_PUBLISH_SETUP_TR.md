# Google Play yayınını tamamlama — Yurdunu Bil 0.6.3

## Repo tarafında hazır
- applicationId: tr.yurdunubil.app
- versionCode: 14
- versionName: 0.6.3
- Debug APK ve Release AAB CI adımları mevcut.
- Release AAB, dört GitHub Actions secret'ı tanımlanırsa otomatik imzalanır.
- Mağaza metni: docs/PLAY_STORE_LISTING_TR.md
- Gizlilik metni: docs/PRIVACY_POLICY_TR.md
- GitHub Pages gizlilik sayfası: docs/privacy/index.html

## Bir kere yapılacaklar
1. GitHub repository Settings > Pages > Source kısmında GitHub Actions seçin.
2. pages.yml çalıştıktan sonra oluşan HTTPS sayfa adresini Google Play Console'daki Privacy Policy alanına girin.
3. İlk yayın için bir Android upload keystore oluşturun.
4. GitHub Actions secrets ekleyin:
   - YB_KEYSTORE_BASE64
   - YB_KEYSTORE_PASSWORD
   - YB_KEY_ALIAS
   - YB_KEY_PASSWORD
5. Actions'tan Android Native CI'ı çalıştırın ve yurdunu-bil-v0.6.3-aab çıktısını alın.
6. Google Play Console'da yeni uygulamayı oluşturup AAB'yi internal testing kanalına yükleyin.
7. Data Safety, Content Rating, Target Audience, App Access ve Store Listing bölümlerini tamamlayın.
8. Gerçek cihazda kayıt, giriş, şifre kurtarma, bildirim, Arena ve sosyal özellikleri test edin.
9. Internal testing sonrası production yayını başlatın.

## Upload keystore örneği
keytool -genkeypair -v -keystore yurdunu-bil-upload.jks -alias yurdunubil -keyalg RSA -keysize 2048 -validity 10000

PowerShell base64:
[Convert]::ToBase64String([IO.File]::ReadAllBytes(".\yurdunu-bil-upload.jks"))

Keystore dosyasını ve parolasını güvenli yerde saklayın; repository'ye yüklemeyin.

## Not
İmzalı AAB hazır olsa bile mağaza yayını otomatik gerçekleşmez. Google Play Console'da gerekli mağaza, veri güvenliği, içerik ve hedef kitle beyanları tamamlanmalıdır.