# Yurdunu Bil — Google Play Release Checklist

Bu belge Yurdunu Bil'in ilk Google Play yayını ve sonraki güncellemeleri için operasyon notudur.

## Uygulama sürümü

- Application ID: `tr.yurdunubil.app`
- İlk public sürüm hedefi: `1.0.0`
- Version code: `12`
- Target SDK: `36` (Android 16)
- Minimum SDK: `26`
- Dağıtım paketi: Android App Bundle (`.aab`)

## CI kontrolü

Her release adayı için aynı pipeline şu dört kontrolü çalıştırır:

1. Debug APK derleme
2. Release AAB derleme
3. Android lint
4. JVM unit testleri

Question bank integrity testleri soru ID'lerinin benzersizliğini, seçenek/doğru cevap yapılarını, açıklamaları ve kütüphane konu kapsamasını denetler.

## İmzalama

Keystore dosyası ve parolaları Git deposuna koyulmaz. Release build, aşağıdaki değişkenlerin tamamı verilirse imzalanır:

- `RELEASE_KEYSTORE_FILE`
- `RELEASE_KEYSTORE_PASSWORD`
- `RELEASE_KEY_ALIAS`
- `RELEASE_KEY_PASSWORD`

Play App Signing kullanılması önerilir. Upload key ile imzalanmış AAB Play Console'a yüklenmelidir.

## Play Console

Yayınlamadan önce mağaza kaydı, uygulama içeriği, yaş/içerik derecelendirmesi, veri güvenliği beyanı, gizlilik politikası URL'si, uygulama erişimi bilgileri ve ekran görüntüleri tamamlanmalıdır.

Yeni bir kişisel Play geliştirici hesabı 13 Kasım 2023'ten sonra oluşturulduysa Google'ın güncel kuralı gereği üretim erişiminden önce kapalı testte en az 12 test kullanıcısının arka arkaya 14 gün boyunca teste kayıtlı kalması gerekir. Bu koşul uygulama koduyla atlanamaz.

## İlk yayın sırası

1. Signed AAB üret.
2. Play Console'a internal test sürümü yükle.
3. Gerçek cihazlarda login, e-posta doğrulama, kütüphane, Atlas, test, yanlışlar, bildirimler ve Arena akışlarını kontrol et.
4. Closed testing'e geç ve gerekiyorsa 12 testçi / 14 günlük şartı tamamla.
5. Store listing + Data Safety + privacy policy + app access alanlarını son kez kontrol et.
6. Production access alındıktan sonra 1.0.0 sürümünü production'a gönder.

## Sonraki güncellemeler

Her güncellemede version code artırılır. Kullanıcıların yerel çalışma verileri geriye dönük uyumlu tutulur. Ortak soru havuzunda ID değiştirip mevcut soruları yeni soru gibi göstermemeye dikkat edilir.
