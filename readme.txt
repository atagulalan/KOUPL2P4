Araç Alış-Satış Programı

Ata Gülalan		-	160202034
Oğuzhan Türker	-	160202015

Bu readme.txt dosyası, Araç Alış-Satış Programı projesine aittir.
Bu paket, kaynak kodu ile aynı dizin içerisinde bulunacaktır.


1-PAKETİN İÇERİĞİ:
----------
160202034_160202015.txt - Projenin tek dosyaya indirgenmiş salt kaynak kodu.
readme.txt - Bu dosya.
kaynak.zip - Projenin kaynak kodunun ve yardımcı dosyaların ziplenmiş hali.
rapor.pdf - Proje raporu.
----------


2-SİSTEM GEREKSİNİMLERİ:
-------------------
java - Oracle Java™ - http://java.com/
-------------------


3-PROJEYİ ÇALIŞTIRMAK:
-------------------
Paket içeriğini, yukarıda görebilirsiniz.

Bu kod, iki adet Windows kurulu makinede çalıştırıldı.

Bu iki durumda da, kod, herhangi bir hata vermeksizin, daha önceden
belirlenen kriterlere uygun çalıştı.

Ön gereklilik:
Projenin çalıştırıldığı makinede, MySQL sunucusu kurulu, root
kullanıcısının tüm haklara sahip olması ve şifresinin olmaması
beklenmektedir. 

Projeyi çalıştırmak için IntelliJ Idea kullanıldı.

Önemli Not: MySQL Connector paketini projeye dahil etmelisiniz.
Bunun için proje ayarlarına girip Kütüphane -> Yeşil + Butonuna
tıklayarak proje dosyaları içinde yer alan 
mysql-connector-java-8.0.11.jar dosyasını ekleyin.
-------------------


4-KODU DERLEMEK:
------------------
Artık bilgisayarımızda kurulu olan Java ile kodu kolayca derleyebiliriz.

Projeyi derlemek için IntelliJ Idea'da sağ üstteki Build butonuna
tıklayabilirsiniz.

Derleme bittikten sonra 3-PROJEYİ ÇALIŞTIRMAK kısmındaki yönergeleri
izleyerek kodu çalıştırabilirsiniz.
------------------


5- PARAMETRELER
---------------------------
Kodun çalışması için başlangıçta herhangi bir parametre gerekmiyor.
------------------


6- PROGRAMIN KULLANIMI
-----------------------------
Araç Alış-Satış Programı, açılışında ilanların bulunduğu bir tabloyu
size gösterir. Bu tabloda düzenleme yapmak için herhangi bir hücreye
çift tıklayabilirsiniz.

# Hücre düzenlemeleri ile ilgili
# - ID hücresi düzenlenemez.
# - Marka hücresi düzenlenmeden Model hücresi düzenlenemez
# - Model hücresi düzenlenebilir olduğunda, model düzenlenmeden herhangi
#   bir hücre düzenlenemez.

İlan Ekleme Formu
İlan eklemek istediğiniz takdirde, programın sol üst köşesinde bulunan
"Yeni Kayıt" butonuna tıklayarak ilan ekleme formunu açabilirsiniz.
Bu formda size ilanın ve arabanın özelliklerini soracaktır.
İlanın başarıyla eklenebilmesi için KM ve Fiyat kısmı tam sayı olmalıdır.
Esneklik açısından arabanın özellikleri bu formda olacaktır.
Belirtilen özelliklere sahip bir araç bulunamadı ise kullanıcıya "Yeni
araç oluşturulsun mu?"" sorusu sorulacak ve kullanıcının verdiği yanıta
göre araç oluşturulacaktır.

Araba Formu
Araba formunda kullanıcı, istediği özelliklere sahip aracı ekleyebilir,
daha önceden eklenmiş araç üstünde değişiklik yapabilir veya silebilir.

Renk/Şehir/Vites Türü/Yakıt Türü Formları
Tamamının tasarımı aynıdır.
Bu formda kullanıcı, ilgili tabloya yeni bir kayıt ekleyebilir, eski
bir kaydı düzenleyebilir veya silebilir.

Filtreleme Formu
Bu formda ilgili kriterlere göre, bir ya da daha fazla filtreleme işlemi
yapılabilir.
Filtrenin çalışabilmesi için öncelikle istenen filtrenin yanındaki kutuyu
tiklemeniz gerekiyor.
İstenen filtreler yapıldıktan sonra "Filtrele" butonuna tıklayarak
filtrelenmiş sonucu tabloda görebilirsiniz.
Filtreyi kaldırmak istiyorsanız filtreleme formundaki tüm tikleri kaldırıp
filtrele butonuna tekrar tıklamanız gerekiyor.

Yenile Butonu
Eğer tablodaki veri filtrelenmiş ise filtreli halini yeniler, aksi halde
filtresiz, tüm verileri veritabanından çeker.