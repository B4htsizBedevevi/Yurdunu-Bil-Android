# Yurdunu Bil — 0.6.3 yayın kontrol listesi

## Teknik
- [ ] GitHub Actions debug APK build başarılı
- [ ] Release AAB build başarılı
- [ ] Release AAB imzalı
- [ ] versionCode 14 / versionName 0.6.3
- [ ] Uygulama açılışında crash yok
- [ ] Kayıt / giriş / şifre yenileme test edildi
- [ ] Aynı e-posta ile tekrar kayıt UX'i test edildi
- [ ] Kullanıcı adı uygunluk kontrolü test edildi
- [ ] Avatar seçimi ve onboarding test edildi
- [ ] Bildirim izni ve test bildirimi Android bildirim panelinde görüldü
- [ ] Admin FCM gönderimi gerçek cihazda doğrulandı
- [ ] Otomatik 09:00 / 14:00 / 20:00 bildirimleri gerçek cihazda doğrulandı
- [ ] Telefon yeniden başlatıldıktan sonra otomatik bildirim planı yeniden kuruldu
- [ ] Arena eşleşme / cevap / bitiş akışı test edildi
- [ ] Sosyal merkez / arkadaş istekleri test edildi

## Google Play
- [ ] Mağaza uygulama adı: Yurdunu Bil
- [ ] Kısa açıklama ve tam açıklama eklendi
- [ ] Uygulama ikonu 512x512 PNG
- [ ] Telefon ekran görüntüleri hazır
- [ ] Gizlilik politikası herkese açık HTTPS URL'de
- [ ] Veri Güvenliği (Data Safety) formu dolduruldu
- [ ] İçerik derecelendirme anketi tamamlandı
- [ ] Hedef kitle ve yaş beyanları tamamlandı
- [ ] Destek e-postası ve geliştirici iletişim bilgileri tamamlandı

## GitHub Actions release imzası
Release AAB'nin imzalanması için GitHub Actions secrets:
- YB_KEYSTORE_BASE64
- YB_KEYSTORE_PASSWORD
- YB_KEY_ALIAS
- YB_KEY_PASSWORD

Keystore kaybedilmemeli. Google Play'de aynı uygulamanın sonraki güncellemeleri için imza anahtarının güvenliği kritik önem taşır.

## Son yayın öncesi test cihazı
Temiz kurulum + yükseltme kurulumu olmak üzere en az iki senaryo çalıştırılmalıdır.
