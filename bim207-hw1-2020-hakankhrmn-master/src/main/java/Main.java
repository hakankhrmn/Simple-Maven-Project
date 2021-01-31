
import java.io.*;
import java.util.*;

public class Main{

    public String[] kelimeList;
    public Main(String fileName, int topN) throws IOException {
        String text;
        String metin="";

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));

        while ((text = in.readLine()) != null){
            text = text.replaceAll("\\p{Punct}", "").toLowerCase();
            metin+=text+" ";

        }

        in.close();
        metin=metin.substring(0,(int)metin.length()-1).replaceAll("  "," ");

        //metin kelime dizisine
        kelimeList=metin.split(" ");

        computeAvgLengthByFirstChar();

        Set pairs = calculateMinPairDist();
        int counter=1;
        for (Object s: pairs){
            System.out.println(s);
            if(counter==topN)break;
            counter++;
        }
    }

  
    private void computeAvgLengthByFirstChar() {

        //karakterdizisinde kelimelerin ilk harfleri tekrarlı olarak bulunur.Her kelimenin ilk harfini içerir.
        String[] karakterDizisi = new String[kelimeList.length];
        for (int i =0;i<kelimeList.length;i++){
            karakterDizisi[i]= String.valueOf(kelimeList[i].charAt(0));
        }

        //harfleri sıralamak ve saymak için karakter setine kelime listten harfleri aktardım. Buradaki harfler tekrar etmez ve alfabetik sıralıdır.
        Set<String> setKarakter = new HashSet<String>(Arrays.asList(karakterDizisi));

        //harflerin konumunu elde edebilmek için karakter setini sıralı harf dizisine aldım.
        String[] siraliHarfDizisi=new String[setKarakter.size()];
        setKarakter.toArray(siraliHarfDizisi);

        //aynı harfle başlayan kelimelerin uzunlukları toplamlarının dizisi
        int[] kelimelengthdizisi = new int[siraliHarfDizisi.length];
        for (int i =0;i<kelimeList.length;i++){
            for (int k=0;k<siraliHarfDizisi.length;k++) {
                if (siraliHarfDizisi[k].equals(String.valueOf(kelimeList[i].charAt(0)))) {
                    kelimelengthdizisi[k]+=kelimeList[i].length();

                }
            }
        }

        //sonucun hesaplanıp yazdırılması
        System.out.println("InitialCharacter\tAverageLength");
        int c=0;
        for (String s:setKarakter){
            double toplam=kelimelengthdizisi[c];
            double tekrar=Collections.frequency(Arrays.asList(karakterDizisi), s);

            double average=  (toplam/tekrar);
            System.out.println(siraliHarfDizisi[c]+"\t\t\t"+ average);
            c++;
        }
        System.out.println(" ");
    }

    private Set calculateMinPairDist() {

        //pair tekrarı olmaması için pair seti oluşturdum
        Set<String> pairseti = new HashSet<>();
        for(int i=0; i<kelimeList.length;i++){
            for (int j=i+1;j<kelimeList.length;j++){
                if(!kelimeList[i].equals(kelimeList[j])){
                    pairseti.add(kelimeList[i]+"-"+kelimeList[j]);
                }
            }
        }

        //kullanım kolaylığı açısından pairseti setini pairset dizisine e aktardım
        String[] pairset =new String[pairseti.size()];
        pairseti.toArray(pairset);

        //minimum mesafe hesaplama
        ArrayList<Integer> distanceArray=new ArrayList<Integer>();

        for (String s:pairseti){
            int distance=0;

            //pairs dizisinde 2 item vardır. Bunlar pair1 ve pair2 dir
            String[] pairs=s.split("-");

            //t1indexes ve t2indexes pair lerin kelime listesindeki indexlerini eklemek için oluşturdum. bu indexleri mesafe hesaplarken kullanacağız.
            ArrayList<Integer> t1indexes=new ArrayList<Integer>();
            ArrayList<Integer> t2indexes=new ArrayList<Integer>();

            //1. pair in indexleri t1indexes e ekleniyor
            for (int i=0;i<kelimeList.length;i++){
                if(pairs[0].equals(kelimeList[i])){
                    t1indexes.add(i);

                }

            }

            //t2indexes bazı koşullara göre dolduruluyor
            for (int i=0;i<t1indexes.size();i++){

                //eğer 1. pairden metinde 1 tane varsa metinde geçen ilk 2. pairin indexi t2indexes e eklenir ve looptan çıkılır.
                // (2. pair metinde 1 den fazla geçebileceği için bu işlem var)
                if (t1indexes.size()==1){
                    for (int j = t1indexes.get(i)+1; j<kelimeList.length; j++){
                        if((kelimeList[j].equals(pairs[1]))){
                            t2indexes.add(j);
                            break;
                        }

                    }
                }
                if (t1indexes.size()!=1){
                    //eğer 1. pair metinde geçen son 1. pair değilse for döngüsündeki 2. koşulu sağlamak için
                    if (i!=t1indexes.size()-1){
                        for (int j = t1indexes.get(i)+1; j<t1indexes.get(i+1); j++){
                            if((kelimeList[j].equals(pairs[1]))){
                                t2indexes.add(j);
                                break;
                            }

                            // 1. pair in metinde geçme sayısı 2. paire göre fazla olabiliyor. Bu yüzden metinde geçen 1. pairden sonra 2. pair yoksa t2indexes a 1. pairin indexini yazdırıyoruz.
                            // Sonradan mesafe hesaplaması hesaplaması için pair lerin indekleri çıkarılacağından böyle bir durumda mesafe 0 olarak hesaplanıyor ve problem oluşmuyor.
                            //Sonuç olarak t1indexes ve t2indexes size ları pair sayıları farklı olsa bile birbirine eşit oluyor
                            if ((j==t1indexes.get(i+1)-1)&&(!t2indexes.contains(j))){
                                t2indexes.add(t1indexes.get(i));
                                break;
                            }
                        }
                    }

                    //eğer 1. pair metinde geçen son 1. pair ise for döngüsündeki 2. koşulu sağlamak için
                    if (i==t1indexes.size()-1){
                        for (int j = t1indexes.get(i)+1; j<kelimeList.length; j++){
                            if((kelimeList[j].equals(pairs[1]))){
                                t2indexes.add(j);
                                break;
                            }

                            // 1. pair in metinde geçme sayısı 2. paire göre fazla olabiliyor. Bu yüzden metinde geçen 1. pairden sonra 2. pair yoksa t2indexes a 1. pairin indexini yazdırıyoruz.
                            // Sonradan mesafe hesaplaması hesaplaması için pair lerin indekleri çıkarılacağından böyle bir durumda mesafe 0 olarak hesaplanıyor ve problem oluşmuyor.
                            //Sonuç olarak t1indexes ve t2indexes size ları pair sayıları farklı olsa bile birbirine eşit oluyor
                            if ((j==kelimeList.length-1)&&(!t2indexes.contains(j))){
                                t2indexes.add(t1indexes.get(i));
                                break;
                            }
                        }
                    }
                }
            }

            //pair mesafeleri toplanıp distanceArrey e eklenir
            if(t1indexes.size()==t2indexes.size()){
                for (int i=0;i<t1indexes.size();i++) {
                    if (t1indexes.get(i) < t2indexes.get(i)) {
                        distance += t2indexes.get(i) - t1indexes.get(i);
                    }
                }
            }
            distanceArray.add(distance);
        }

        // factor formülü
        ArrayList<Double> factoryFormula=new ArrayList<Double>();
        int k=0;
        for (String s:pairseti) {
            String[] pairs = s.split("-");
            int countt1=Collections.frequency(Arrays.asList(kelimeList), pairs[0]);
            int countt2=Collections.frequency(Arrays.asList(kelimeList), pairs[1]);

            double factor=((countt1*countt2)/(1+(Math.log(distanceArray.get(k)))));
            factoryFormula.add(factor);
            k++;
        }

        //arraylari çıktıya uygun olması için sıralama
        int n=factoryFormula.size();
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (factoryFormula.get(j) > factoryFormula.get(j + 1)) {
                    // swap arr[j+1] and arr[j]
                    double temp = factoryFormula.get(j);
                    factoryFormula.set(j, factoryFormula.get(j + 1));
                    factoryFormula.set(j + 1, temp);

                    String template = pairset[j];
                    pairset[j] = pairset[j + 1];
                    pairset[j + 1] = template;
                }
            }
        }

        //return edilecek set
        Set<String> cikti = new LinkedHashSet<String>();

        for (int i=pairset.length-1;i>-1;i--){
            cikti.add("Pair{t1='"+pairset[i].substring(0,pairset[i].indexOf("-"))+"', t2='"+pairset[i].substring(pairset[i].indexOf("-")+1)+"', factor="+ factoryFormula.get(i) +"}");
        }

        return cikti;
    }




    public static void main(String[] args) throws IOException {
        new Main(args[0],Integer.parseInt(args[1]));

    }


}
