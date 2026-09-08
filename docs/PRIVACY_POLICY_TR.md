# Yurdunu Bil — Gizlilik Politikası Taslağı

**Son güncelleme:** 8 Eylül 2026

Yurdunu Bil, KPSS Türkiye coğrafyası çalışmayı sağlayan bir eğitim uygulamasıdır. Bu metin Google Play mağaza kaydı için kullanılmadan önce uygulamanın gerçek veri akışı ve yürürlükteki mevzuatla son kez kontrol edilmelidir.

## Toplanan bilgiler

### Hesap bilgileri

Hesap oluşturma ve giriş için e-posta adresi ve kimlik doğrulama bilgileri Supabase Auth altyapısında işlenebilir. Uygulama parolanın kendisini yerel uygulama verisi olarak tutmayı amaçlamaz.

### Çalışma ilerlemesi

Çözülen soru sayısı, doğru/yanlış sayıları, XP, seri, görülen sorular, yanlış soru kimlikleri ve konu bazlı performans gibi çalışma verileri uygulamanın yerel DataStore alanında tutulur. Çevrim içi özellikler etkinleştirildiğinde ilgili hesap ilerlemesi sunucu tarafındaki uygulama tablolarıyla eşleşebilir.

### Bildirimler

Kullanıcı çalışma veya Arena bildirimlerine izin verirse bildirim göndermek için cihaz bildirimiyle ilişkili teknik kayıtlar kullanılabilir. Bildirim izni Android tarafından cihaz üzerinde yönetilir.

## Verilerin kullanım amacı

Veriler; oturum açmayı sağlamak, çalışma ilerlemesini saklamak, yanlışları ve zayıf konuları göstermek, günlük çalışma deneyimini sunmak ve çevrim içi rekabet özelliklerini çalıştırmak amacıyla kullanılabilir.

## Veri paylaşımı

Yurdunu Bil, kullanıcı verilerini reklam amacıyla satmayı amaçlamaz. Kimlik doğrulama ve çevrim içi uygulama özelliklerinde kullanılan üçüncü taraf servisler yalnızca ilgili hizmeti sunmak için gerekli verileri işleyebilir.

## Saklama ve silme

Yerel çalışma verileri uygulama cihazında tutulabilir ve uygulama verilerinin temizlenmesiyle kaldırılabilir. Hesaba bağlı sunucu verileri için kullanıcı, uygulamanın hesap/veri silme kanalından talepte bulunabilir. Yayına çıkmadan önce bu kanalın gerçek iletişim adresi Play Console'daki beyanlarla aynı olacak şekilde eklenmelidir.

## Güvenlik

Parola, servis anahtarı veya gizli kimlik bilgileri uygulama kaynak koduna konulmamalıdır. Supabase tarafında yalnızca gerekli istemci erişimi kullanılır; ayrıcalıklı servis anahtarları mobil uygulamaya gömülmemelidir.

## Çocuklar

Uygulama genel eğitim amacı taşır ve hedef kitlesi KPSS hazırlığı yapan kullanıcılardır. Yaş hedeflemesi ve çocuklara yönelik içerik beyanı Play Console'da gerçek dağıtım stratejisine göre yapılmalıdır.

## İletişim

Yayın öncesinde bu bölüme uygulama sahibinin güncel destek e-posta adresi eklenmelidir.

## Değişiklikler

Bu politika, uygulamanın veri akışı veya yasal gereklilikleri değiştiğinde güncellenebilir.
