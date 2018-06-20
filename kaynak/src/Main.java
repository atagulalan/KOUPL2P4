import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.Objects;

/*

Proje 4

Ata Gülalan - Oğuzhan Türker
160202034   - 160202015

Kaynakça
JAVA Swing Table Example | Examples Java Code Geeks - 2018
https://examples.javacodegeeks.com/desktop-java/swing/java-swing-table-example/
java - How to add row dynamically in JTable - Stack Overflow
https://stackoverflow.com/questions/22371720/how-to-add-row-dynamically-in-jtable
Table Cell Listener « Java Tips Weblog
https://tips4java.wordpress.com/2009/06/07/table-cell-listener/
swing - JTable cell value change listener - Stack Overflow
https://stackoverflow.com/questions/7604944/jtable-cell-value-change-listener
java - JTable, disable user column dragging - Stack Overflow
https://stackoverflow.com/questions/17641123/jtable-disable-user-column-dragging
java - Adding items to a JComboBox - Stack Overflow
https://stackoverflow.com/questions/17887927/adding-items-to-a-jcombobox
java - "Field can be converted to a local variable" message appearing when setting action bar colour - Stack Overflow
https://stackoverflow.com/questions/31713073/field-can-be-converted-to-a-local-variable-message-appearing-when-setting-acti
java - How to clear contents of a jTable ? - Stack Overflow
https://stackoverflow.com/questions/3879610/how-to-clear-contents-of-a-jtable
java - Getting values from JTable cell - Stack Overflow
https://stackoverflow.com/questions/16395939/getting-values-from-jtable-cell
java - JTable: Detect cell data change - Stack Overflow
https://stackoverflow.com/questions/6889694/jtable-detect-cell-data-change
java - Suppress warning ~ Actual value of parameter X is always Y - Stack Overflow
https://stackoverflow.com/questions/48734714/suppress-warning-actual-value-of-parameter-x-is-always-y
How To Make A Simple Textbox In Java - Java | Dream.In.Code
http://www.dreamincode.net/forums/topic/90160-how-to-make-a-simple-textbox-in-java/
java - Align text in JLabel to the right - Stack Overflow
https://stackoverflow.com/questions/12589494/align-text-in-jlabel-to-the-right#12589611
swing - Java - How to add a JOptionPane for Yes and No options - Stack Overflow
https://stackoverflow.com/questions/9228542/java-how-to-add-a-joptionpane-for-yes-and-no-options
java - How to programmatically close a JFrame - Stack Overflow
https://stackoverflow.com/questions/1234912/how-to-programmatically-close-a-jframe
java - How to update a JLabel text? - Stack Overflow
https://stackoverflow.com/questions/17456401/how-to-update-a-jlabel-text
java - can i add the combobox into particular cell of the JTable? - Stack Overflow
https://stackoverflow.com/questions/2543554/can-i-add-the-combobox-into-particular-cell-of-the-jtable
How to Use Tables (The Java™ Tutorials > Creating a GUI With JFC/Swing > Using Swing Components)
https://docs.oracle.com/javase/tutorial/uiswing/components/table.html
java - Checking if an item already exists in a JComboBox? - Stack Overflow
https://stackoverflow.com/questions/8899051/checking-if-an-item-already-exists-in-a-jcombobox#8899169
java - Remove Duplicated Items JComboBox - Stack Overflow
https://stackoverflow.com/questions/27347189/remove-duplicated-items-jcombobox
Writing Turkish characters to MySQL using WampServer and Java - Kavaoil
http://www.kavaoil.com/writing-turkish-characters-to-mysql-using-wampserver-and-java/
*/

@SuppressWarnings("FieldCanBeLocal")
public class Main {
    private static Connection connect = null;
    private static Statement statement = null;
    private static ResultSet resultSet = null;
    private static String url = "jdbc:mysql://localhost:3306/proje4?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&characterEncoding=utf-8";
    private static String username = "root";
    private static String password = "";
    private static boolean changeMarka = false;
    private static boolean noFilter = true;
    private static String lastQuery = "";

    private static void initDB() {
        System.out.println("Veritabanına Bağlanılıyor...");
        try (Connection ignored = DriverManager.getConnection(url, username, password)) {
            System.out.println("OK! Veritabanına Bağlanıldı!");
        } catch (SQLException e) {
            throw new IllegalStateException("Hata! Veritabanına Bağlanılamıyor.", e);
        }
    }

    private static void query(String query, String type) {
        try {
            connect = DriverManager.getConnection(url, username, password);
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            writeResultSet(resultSet, type);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    static void filterQuery(String q) {
        noFilter = false;
        lastQuery = q;
        query(q,"getTable");
    }

    static void repeatFilter() {
        noFilter = false;
        query(lastQuery,"getTable");
    }

    static void update(String query) {
        try {
            connect = DriverManager.getConnection(url, username, password);
            statement = connect.createStatement();
            statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    static void deleteIlan(String ID){
        update("DELETE FROM tbl_ilan WHERE IlanID = "+ID);
        getAllIlan();
    }

    @SuppressWarnings("SameParameterValue")
    private static String queryGetRow(String query, String type) {
        try {
            connect = DriverManager.getConnection(url, username, password);
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.next();
            if (Objects.equals(type, "getArabaID")) {
                return resultSet.getString("ArabaID");
            }
        } catch (Exception e) {
            //Eğer buraya düşüldü ise tablo boş dönmüştür
            return "ERROR";
        }
        return "ERROR";
    }

    public static void main(String[] args) {
        initDB();
        Ekran.ekraniAc();
        reloadEverything();
    }

    static void addAraba(Object marka, Object model, Object renk, Object vites, Object yakit) {
        update("INSERT INTO tbl_araba VALUES (NULL, '"+marka+"', '"+model+"', '"+vites+"', '"+yakit+"', '"+renk+"')");
    }

    static void addIlan(Object ad, Object sehir, Object marka, Object model, Object renk, Object vites, Object yakit, Object km, Object fiyat) {
        String ID = getArabaID(marka, model, yakit, vites, renk);
        ad = ad.toString();
        if(Objects.equals(ID,"ERROR")){
            if(Ekran.giveError("noSuchVehicle").equals("YENI")){
                addAraba(marka,model,renk,vites,yakit);
                addIlan(ad, sehir, marka, model, renk, vites, yakit, km, fiyat);
                reloadEverything();
            }
        }else{
            System.out.println(ID + ", " + ad + ", " + sehir + ", " + km + ", " + fiyat);
            update("INSERT INTO `tbl_ilan` VALUES (NULL, '"+ad+"', '"+fiyat+"', '"+km+"', CURRENT_TIMESTAMP, '"+ID+"', '"+sehir+"')");
            Ekran.closeNewForm();
        }
    }

    static void getAllIlan() {
        query("SELECT *\n" +
                        "FROM tbl_ilan\n" +
                        "INNER JOIN tbl_araba ON tbl_ilan.Ilan_ArabaID = tbl_araba.ArabaID " +
                        "INNER JOIN tbl_sehir ON tbl_ilan.Ilan_SehirID = tbl_sehir.SehirID " +
                        "INNER JOIN tbl_renk ON tbl_araba.Araba_RenkID = tbl_renk.RenkID " +
                        "INNER JOIN tbl_vitesturu ON tbl_araba.Araba_VitesTuruID = tbl_vitesturu.VitesTuruID " +
                        "INNER JOIN tbl_yakitturu ON tbl_araba.Araba_YakitTuruID = tbl_yakitturu.YakitTuruID " +
                        "ORDER BY IlanID ASC;",
                "getTable");
        changeMarka = false;
        noFilter = true;
    }

    private static Integer findIndexOfCombo(Integer row, Integer col){
        String aranan = Ekran.getTable().getValueAt(row, col).toString();
        int mdl = -1;
        if(col==5){
            mdl = Ekran.getIdFromString(Ekran.sehirler,aranan);
        }else if(col==8){
            mdl = Ekran.getIdFromString(Ekran.renkler,aranan);
        }else if(col==9){
            mdl = Ekran.getIdFromString(Ekran.vitesler,aranan);
        }else if(col==10){
            mdl = Ekran.getIdFromString(Ekran.yakitlar,aranan);
        }
        return mdl;
    }

    private static void editIlan(String id, String ad, String fiyat, String km, String tarih, String araba, String sehir){
        update("UPDATE tbl_ilan SET " +
                "Ilan_Adi = '"+ad+"', " +
                "Ilan_Fiyat = '"+fiyat+"', " +
                "Ilan_Km = '"+km+"', " +
                "Ilan_Tarih = '"+tarih+"', " +
                "Ilan_ArabaID = '"+araba+"', " +
                "Ilan_SehirID = '"+sehir+"'" +
                " WHERE IlanID = "+id);
    }

    static String getArabaID(Object marka, Object model, Object yakit, Object vites, Object renk){
        return queryGetRow("SELECT * FROM tbl_araba " +
                "WHERE Araba_Marka='" + marka + "' " +
                "AND Araba_Model='" + model + "' " +
                "AND Araba_YakitTuruID=" + yakit + " " +
                "AND Araba_VitesTuruID=" + vites + " " +
                "AND Araba_RenkID=" + renk, "getArabaID");
    }



    static void updateCell(Integer row, Integer col) {
        String ID = Ekran.getTable().getValueAt(row, 0).toString();
        String ad = Ekran.getTable().getValueAt(row, 1).toString();
        String fiyat = Ekran.getTable().getValueAt(row, 2).toString();
        String km = Ekran.getTable().getValueAt(row, 3).toString();
        String tarih = Ekran.getTable().getValueAt(row, 4).toString();
        String CELL = Ekran.getTable().getValueAt(row, col).toString();
        String marka = Ekran.getTable().getValueAt(row, 6).toString();
        String model = Ekran.getTable().getValueAt(row, 7).toString();
        @SuppressWarnings("ConstantConditions")
        String renk = (col == 8) ? String.valueOf(Ekran.getIdFromString(Ekran.renkler, Ekran.editRenkler.getSelectedItem().toString()))
                : String.valueOf(findIndexOfCombo(row, 8));
        @SuppressWarnings("ConstantConditions")
        String vites = (col == 9) ? String.valueOf(Ekran.getIdFromString(Ekran.vitesler, Ekran.editVitesler.getSelectedItem().toString()))
                : String.valueOf(findIndexOfCombo(row, 9));
        @SuppressWarnings("ConstantConditions")
        String yakit = (col == 10) ? String.valueOf(Ekran.getIdFromString(Ekran.yakitlar, Ekran.editYakitlar.getSelectedItem().toString()))
                : String.valueOf(findIndexOfCombo(row, 10));
        @SuppressWarnings("ConstantConditions")
        String sehir = (col == 5) ? String.valueOf(Ekran.getIdFromString(Ekran.sehirler, Ekran.editSehirler.getSelectedItem().toString()))
                : String.valueOf(findIndexOfCombo(row, 5));

        if(col==6 || col==7 || col==8 || col==9 || col==10) {

            if (col == 6) {
                Ekran.editModeller.removeAllItems();
                Ekran.getTable().setValueAt("", row, 7);
                query("select distinct Araba_Model from tbl_araba where Araba_Marka='"+marka+"'", "fillModels");
                changeMarka = true;
                Ekran.setEditSatir(row);
            } else {
                if (col == 7) {
                    changeMarka = false;
                }
                System.out.println("İlandaki araba değiştirildi");
                //Eğer araba var ise diye kontrol et
                String ArabaID = getArabaID(marka,model,yakit,vites,renk);
                if (ArabaID.equals("ERROR")) {
                    if (Ekran.giveError("noSuchVehicle").equals("YENI")) {
                        System.out.println("Yeni araç oluşturuluyor...");
                        System.out.println(marka+" "+model+" "+renk+" "+vites+" "+yakit);
                        addAraba(marka,model,renk,vites,yakit);
                        ArabaID = getArabaID(marka,model,yakit,vites,renk);
                        editIlan(ID, ad, fiyat, km, tarih, ArabaID, sehir);
                        reloadEverything();
                    } else {
                        System.out.println("Tablo yenileniyor...");
                        getAllIlan();
                        reloadEverything();
                    }
                } else {
                    System.out.println("Araba ID: " + ArabaID);
                    editIlan(ID, ad, fiyat, km, tarih, ArabaID, sehir);
                }
            }

        }else if(col==1 || col==2 || col==3 || col==4 || col==5){
            System.out.println("İlan değiştirildi " + sehir);
            String ArabaID = getArabaID(marka,model,yakit,vites,renk);
            editIlan(ID, ad, fiyat, km, tarih, ArabaID, sehir);
            getAllIlan();
        }else{
            System.out.println("UPDATE " + ID + " : " + CELL);
        }
    }

    static boolean canChangeMarka(){
        return changeMarka;
    }

    static boolean isNoFilter(){
        return noFilter;
    }


    static void reloadEverything(){
        Ekran.clearEverything();
        getAllIlan();
        query("select * from tbl_renk ORDER BY RenkID ASC;", "getRenk");
        query("select * from tbl_sehir ORDER BY SehirID ASC", "getSehir");
        query("select * from tbl_vitesturu ORDER BY VitesTuruID ASC", "getVites");
        query("select * from tbl_yakitturu ORDER BY YakitTuruID ASC", "getYakit");
        query("select distinct Araba_Marka, Araba_Model FROM tbl_araba", "getAraba");
        query("SELECT * FROM tbl_araba\n" +
                "    INNER JOIN tbl_renk ON tbl_araba.Araba_RenkID = tbl_renk.RenkID \n" +
                "    INNER JOIN tbl_vitesturu ON tbl_araba.Araba_VitesTuruID = tbl_vitesturu.VitesTuruID \n" +
                "    INNER JOIN tbl_yakitturu ON tbl_araba.Araba_YakitTuruID = tbl_yakitturu.YakitTuruID \n" +
                "    ORDER BY ArabaID ASC;", "getArabaND");
    }

    private static void writeResultSet(ResultSet resultSet, String type) throws SQLException {
        if (Objects.equals(type, "getTable")) {
            Ekran.clearTable();
        }
        while (resultSet.next()) {
            if (Objects.equals(type, "getRenk")) {
                String ad = resultSet.getString("Renk");
                String id = resultSet.getString("RenkID");
                Ekran.addItemToCombo(id, ad, type);
            } else if (Objects.equals(type, "getSehir")) {
                String ad = resultSet.getString("Sehir");
                String id = resultSet.getString("SehirID");
                Ekran.addItemToCombo(id, ad, type);
            } else if (Objects.equals(type, "getVites")) {
                String ad = resultSet.getString("Vites_Turu");
                String id = resultSet.getString("VitesTuruID");
                Ekran.addItemToCombo(id, ad, type);
            } else if (Objects.equals(type, "getYakit")) {
                String ad = resultSet.getString("Yakit_Turu");
                String id = resultSet.getString("YakitTuruID");
                Ekran.addItemToCombo(id, ad, type);
            } else if (Objects.equals(type, "getAraba")) {
                String marka = resultSet.getString("Araba_Marka");
                String model = resultSet.getString("Araba_Model");
                String[] araba = {marka, model};
                Ekran.addItemToCombo(araba, marka + " " + model, type);
            } else if (Objects.equals(type, "getArabaND")) {
                String id = resultSet.getString("ArabaID");
                String marka = resultSet.getString("Araba_Marka");
                String model = resultSet.getString("Araba_Model");
                String vites = resultSet.getString("Vites_Turu");
                String yakit = resultSet.getString("Yakit_Turu");
                String renk = resultSet.getString("Renk");
                Ekran.addItemToCombo(id, marka + " " + model + ", " + vites + ", " + yakit + ", " + renk, type);
            } else if (Objects.equals(type, "getTable")) {
                String id = resultSet.getString("IlanID");
                String ad = resultSet.getString("Ilan_Adi");
                String fiyat = resultSet.getString("Ilan_Fiyat");
                String km = resultSet.getString("Ilan_Km");
                String tarih = resultSet.getString("Ilan_Tarih");
                String sehir = resultSet.getString("Sehir");
                String marka = resultSet.getString("Araba_Marka");
                String model = resultSet.getString("Araba_Model");
                String renk = resultSet.getString("Renk");
                String vites = resultSet.getString("Vites_Turu");
                String yakit = resultSet.getString("Yakit_Turu");
                Object[] ilan = {id, ad, fiyat, km, tarih, sehir, marka, model, renk, vites, yakit};
                Ekran.addIlanToTable(ilan);
            } else if (Objects.equals(type, "fillModels")) {
                String ad = resultSet.getString("Araba_Model");
                Ekran.addItemToCombo("", ad, type);
            }
        }
    }
}


class Ekran extends JPanel {
    private static Color arkaplan = new Color(0xf1f2f2);
    private static LinkedJTable tablo = new LinkedJTable(){
        @Override
        public boolean isCellEditable(int row, int column) {
            if (!Main.canChangeMarka()) {
                if (column == 7) {
                    return false;
                }
            } else {
                if (column != 7) {
                    return false;
                }
                if (row != Ekran.getEditSatir()) {
                    return false;
                }
            }
            return Main.isNoFilter() && column != 0;
        }
    };
    private static DefaultTableModel dtm = new DefaultTableModel(0, 0);
    private static JComboBox<ComboItem> arabalar = new JComboBox<>();
    private static JComboBox<ComboItem> arabalarND = new JComboBox<>();
    static JComboBox<ComboItem> renkler = new JComboBox<>();
    static JComboBox<ComboItem> sehirler = new JComboBox<>();
    static JComboBox<ComboItem> vitesler = new JComboBox<>();
    static JComboBox<ComboItem> yakitlar = new JComboBox<>();
    static JComboBox<String> editRenkler = new JComboBox<>();
    static JComboBox<String> editSehirler = new JComboBox<>();
    static JComboBox<String> editVitesler = new JComboBox<>();
    static JComboBox<String> editYakitlar = new JComboBox<>();
    private static JComboBox<String> editMarkalar = new JComboBox<>();
    static JComboBox<String> editModeller = new JComboBox<>();
    //private static JComboBox<ComboItem> sutunlar = new JComboBox<>();
    private static JLabel hataLabeli = new JLabel();
    private static JFrame yeniKayitFormu = new JFrame("Yeni Kayıt Ekle");
    private static int editSatir = -1;
    private static String acikPanel = "";
    private static String setWhat = "";
    private static String deleteWhere = "";

    static int getIdFromString(JComboBox<ComboItem> k, String str){
        int itemExists = 0;
        for (int i = 0; i<k.getItemCount(); i++) {
            itemExists = k.getItemAt(i).toString().equals(str) ? i : itemExists;
            if (itemExists>0) break;
        }
        //System.out.println(str + " " + Integer.valueOf(k.getItemAt(itemExists).getKey()[0].toString()));
        return Integer.valueOf(k.getItemAt(itemExists).getKey()[0].toString());
    }

    private static int getEditSatir(){
        return editSatir;
    }

    static void setEditSatir(int i){
        editSatir=i;
    }

    static void addItemToCombo(String id, String ad, String type) {
        if(Objects.equals(type,"getRenk")){
            renkler.addItem(new ComboItem(id, ad));
            editRenkler.addItem(ad);
        }else if(Objects.equals(type,"getSehir")){
            sehirler.addItem(new ComboItem(id, ad));
            editSehirler.addItem(ad);
        }else if(Objects.equals(type,"getVites")){
            vitesler.addItem(new ComboItem(id, ad));
            editVitesler.addItem(ad);
        }else if(Objects.equals(type,"getYakit")){
            yakitlar.addItem(new ComboItem(id, ad));
            editYakitlar.addItem(ad);
        }else if(Objects.equals(type,"getArabaND")){
            arabalarND.addItem(new ComboItem(id, ad));
        }else if(Objects.equals(type,"fillModels")){
            editModeller.addItem(ad);
        }
    }

    static void addItemToCombo(String id[], String ad, String type) {
        if(Objects.equals(type,"getAraba")){
            arabalar.addItem(new ComboItem(id, ad));
            boolean itemExists = false;
            for (int i = 0; i<editMarkalar.getItemCount(); i++) {
                itemExists = editMarkalar.getItemAt(i).equals(id[0]);
                if (itemExists) break;
            }
            if(!itemExists){ editMarkalar.addItem(id[0]); }
            editModeller.addItem(id[1]);
        }
    }

    static void addIlanToTable(Object arr[]){
        dtm.addRow(arr);
    }

    static LinkedJTable getTable(){
        return tablo;
    }

    static void clearTable(){
        dtm.setRowCount(0);
    }

    static String giveError(String type){
        if(Objects.equals(type,"noSuchVehicle")){
            hataLabeli.setText("Bu özelliklere sahip araç bulunamadı.");
            int selectedOption = JOptionPane.showConfirmDialog(null,
                    "Bu özellikte araç bulunamadı. Yeni araç oluşturulsun mu?",
                    "Seçim",
                    JOptionPane.YES_NO_OPTION);
            if (selectedOption == JOptionPane.YES_OPTION) {
                return "YENI";
            }
        }else if(Objects.equals(type,"noName")){
            hataLabeli.setText("İlan adı boş bırakılamaz.");
        }else if(Objects.equals(type,"noKm")){
            hataLabeli.setText("Kilometre boş bırakılamaz.");
        }else if(Objects.equals(type,"noPrice")){
            hataLabeli.setText("Fiyat boş bırakılamaz.");
        }else{
            hataLabeli.setText("");
        }
        return "";
    }

    static void closeNewForm(){
        yeniKayitFormu.dispatchEvent(new WindowEvent(yeniKayitFormu, WindowEvent.WINDOW_CLOSING));
        Main.getAllIlan();
    }

    private static void clearForms(JTextField ad, JTextField fiyat, JTextField km){
        ad.setText("");
        km.setText("");
        fiyat.setText("");
        renkler.setSelectedIndex(0);
        sehirler.setSelectedIndex(0);
        vitesler.setSelectedIndex(0);
        yakitlar.setSelectedIndex(0);
        arabalar.setSelectedIndex(0);
    }

    static void clearEverything() {
        renkler.removeAllItems();
        sehirler.removeAllItems();
        vitesler.removeAllItems();
        yakitlar.removeAllItems();
        arabalar.removeAllItems();
        arabalarND.removeAllItems();
        editRenkler.removeAllItems();
        editSehirler.removeAllItems();
        editVitesler.removeAllItems();
        editYakitlar.removeAllItems();
        editMarkalar.removeAllItems();
    }

    private static String findAraba(String marka, String model){
        assert arabalar.getSelectedItem() != null;
        if(arabalar.getItemCount()>0){
            assert yakitlar.getSelectedItem() != null;
            String yakit = ((ComboItem)yakitlar.getSelectedItem()).getKey()[0].toString();
            assert vitesler.getSelectedItem() != null;
            String vites = ((ComboItem)vitesler.getSelectedItem()).getKey()[0].toString();
            assert renkler.getSelectedItem() != null;
            String renk = ((ComboItem)renkler.getSelectedItem()).getKey()[0].toString();
            String getID = Main.getArabaID(marka, model, yakit, vites, renk);
            if(getID.equals("ERROR")){
                getID="";
            }
            return getID;
        }
        return "?";
    }

    @SuppressWarnings("Duplicates")
    static void ekraniAc() {
        // - ANA TABLO FORMU - \\
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("Araç Alış-Satış Programı");
        frame.setSize(1100, 700);
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);
        panel.setBackground(arkaplan);

        JButton yeni_kayit = new JButton("Yeni Kayıt");
        yeni_kayit.setBounds(10, 10, 100, 30);
        panel.add(yeni_kayit);

        JButton filtrele = new JButton("Filtrele");
        filtrele.setBounds(120, 10, 100, 30);
        panel.add(filtrele);

        JButton sil = new JButton("Seçili Satırı Sil");
        sil.setBounds(230, 10, 150, 30);
        panel.add(sil);

        JButton yeni_araba = new JButton("Arabalar");
        yeni_araba.setBounds(390, 10, 100, 30);
        panel.add(yeni_araba);

        JButton yeni_renk = new JButton("Renkler");
        yeni_renk.setBounds(500, 10, 100, 30);
        panel.add(yeni_renk);

        JButton yeni_sehir = new JButton("Şehirler");
        yeni_sehir.setBounds(610, 10, 100, 30);
        panel.add(yeni_sehir);

        JButton yeni_yakit = new JButton("Yakıtlar");
        yeni_yakit.setBounds(720, 10, 100, 30);
        panel.add(yeni_yakit);

        JButton yeni_vites = new JButton("Vitesler");
        yeni_vites.setBounds(830, 10, 100, 30);
        panel.add(yeni_vites);

        JButton yenile = new JButton("Yenile");
        yenile.setBounds(980, 10, 100, 30);
        panel.add(yenile);

        String header[] = new String[] {
                "ID",
                "İlan Adı",
                "Fiyat",
                "Kilometre",
                "Tarih",
                "Şehir",
                "Marka",
                "Model",
                "Renk",
                "Vites Türü",
                "Yakıt Türü"
        };

        dtm.setColumnIdentifiers(header);
        tablo.setModel(dtm);
        tablo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablo.getTableHeader().setReorderingAllowed(false);
        tablo.putClientProperty("terminateEditOnFocusLost", true);
        tablo.setPreferredScrollableViewportSize(new Dimension(500, 70));
        tablo.setFillsViewportHeight(true);
        tablo.setColumnSelectionAllowed(false);

        tablo.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(editSehirler));
        tablo.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(editMarkalar));
        tablo.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(editModeller));
        tablo.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(editRenkler));
        tablo.getColumnModel().getColumn(9).setCellEditor(new DefaultCellEditor(editVitesler));
        tablo.getColumnModel().getColumn(10).setCellEditor(new DefaultCellEditor(editYakitlar));

        JScrollPane scrollPane = new JScrollPane(tablo);
        scrollPane.setBounds(0, 50, 1096, 620);
        panel.add(scrollPane);
        frame.setVisible(true);

        /*LABELS*/
        JLabel idLabel = new JLabel("ID:",SwingConstants.RIGHT);
        JLabel adLabel = new JLabel("İlan Adı:",SwingConstants.RIGHT);
        JLabel secLabel = new JLabel("Seç:",SwingConstants.RIGHT);
        JLabel arabaLabel = new JLabel("Araba:",SwingConstants.RIGHT);
        JLabel markaLabel = new JLabel("Marka:",SwingConstants.RIGHT);
        JLabel modelLabel = new JLabel("Model:",SwingConstants.RIGHT);
        JLabel renkLabel = new JLabel("Renk:",SwingConstants.RIGHT);
        JLabel sehirLabel = new JLabel("Şehir:",SwingConstants.RIGHT);
        JLabel vitesLabel = new JLabel("Vites Türü:",SwingConstants.RIGHT);
        JLabel yakitLabel = new JLabel("Yakıt Türü:",SwingConstants.RIGHT);
        JLabel kmLabel = new JLabel("KM:",SwingConstants.RIGHT);
        JLabel fiyatLabel = new JLabel("Fiyat:",SwingConstants.RIGHT);

        idLabel.setBounds(10, 10, 80, 30);
        adLabel.setBounds(10, 10, 80, 30);
        arabaLabel.setBounds(10, 50, 80, 30);
        secLabel.setBounds(10, 50, 80, 30);
        renkLabel.setBounds(10, 90, 80, 30);
        sehirLabel.setBounds(10, 130, 80, 30);
        vitesLabel.setBounds(10, 170, 80, 30);
        yakitLabel.setBounds(10, 210, 80, 30);
        kmLabel.setBounds(10, 250, 80, 30);
        fiyatLabel.setBounds(10, 290, 80, 30);



        // - MINI FORM - \\
        JFrame miniFormu = new JFrame("Ekle/Güncelle/Sil");
        miniFormu.setSize(270, 200);
        miniFormu.setResizable(false);
        miniFormu.setLocationRelativeTo(frame);

        JPanel miniPaneli = new JPanel();
        miniPaneli.setLayout(null);
        miniFormu.add(miniPaneli);
        miniPaneli.setBackground(arkaplan);

        JTextField miniId = new JTextField("", 20);
        JCheckBox miniCB = new JCheckBox("Yeni");

        JButton miniSil = new JButton("Sil");
        miniSil.setBounds(10, 130, 80, 30);
        miniPaneli.add(miniSil);

        JButton miniEkle = new JButton("Güncelle");
        miniEkle.setBounds(100, 130, 150, 30);
        miniPaneli.add(miniEkle);

        JTextField miniAd = new JTextField("", 20);
        miniAd.setBounds(100, 90, 150, 30);
        miniPaneli.add(miniAd);

        JButton arabaSil = new JButton("Sil");
        arabaSil.setBounds(10, 290, 80, 30);

        JButton arabaEkle = new JButton("Güncelle");
        arabaEkle.setBounds(100, 290, 280, 30);

        miniCB.addActionListener(e -> {
            if(miniCB.isSelected()){
                miniId.setText("");
                miniEkle.setText("Ekle");
                if(acikPanel.equals("tbl_araba")){
                    arabaSil.setEnabled(false);
                    arabaEkle.setText("Ekle");
                    arabalarND.setEnabled(false);
                }else{
                    renkler.setEnabled(false);
                    sehirler.setEnabled(false);
                    vitesler.setEnabled(false);
                    yakitlar.setEnabled(false);
                    miniSil.setEnabled(false);
                }
            }else{
                miniEkle.setText("Güncelle");
                if(acikPanel.equals("tbl_araba")){
                    arabaSil.setEnabled(true);
                    arabaEkle.setText("Güncelle");
                    arabalarND.setSelectedIndex(0);
                    arabalarND.setEnabled(true);
                    miniId.setText("1");
                }else{
                    renkler.setSelectedIndex(0);
                    sehirler.setSelectedIndex(0);
                    vitesler.setSelectedIndex(0);
                    yakitlar.setSelectedIndex(0);
                    renkler.setEnabled(true);
                    sehirler.setEnabled(true);
                    vitesler.setEnabled(true);
                    yakitlar.setEnabled(true);
                    miniSil.setEnabled(true);
                    miniId.setText("1");
                }
            }
        });


        arabalarND.addActionListener(e -> {
            assert arabalarND.getSelectedItem() != null;
            if(arabalarND.getItemCount()>0) {
                if (acikPanel.equals("tbl_araba")) {
                    miniId.setText(((ComboItem) arabalarND.getSelectedItem()).getKey()[0].toString());
                }
            }
        });

        renkler.addActionListener(e -> {
            assert renkler.getSelectedItem() != null;
            if(renkler.getItemCount()>0) {
                if (!acikPanel.equals("tbl_araba")){
                    miniId.setText(((ComboItem) renkler.getSelectedItem()).getKey()[0].toString());
                }
            }
        });

        sehirler.addActionListener(e -> {
            assert sehirler.getSelectedItem() != null;
            if(sehirler.getItemCount()>0) {
                if(!acikPanel.equals("tbl_araba")) {
                    miniId.setText(((ComboItem) sehirler.getSelectedItem()).getKey()[0].toString());
                }
            }
        });

        vitesler.addActionListener(e -> {
            assert vitesler.getSelectedItem() != null;
            if(vitesler.getItemCount()>0) {
                if (!acikPanel.equals("tbl_araba")) {
                    miniId.setText(((ComboItem) vitesler.getSelectedItem()).getKey()[0].toString());
                }
            }
        });

        yakitlar.addActionListener(e -> {
            assert yakitlar.getSelectedItem() != null;
            if(yakitlar.getItemCount()>0) {
                if(!acikPanel.equals("tbl_araba")){
                    miniId.setText(((ComboItem) yakitlar.getSelectedItem()).getKey()[0].toString());
                }

            }
        });

        miniSil.addActionListener(e -> {
            System.out.println("DELETE FROM "+acikPanel+" WHERE "+deleteWhere+" = "+miniId.getText()+";");
            Main.update("DELETE FROM "+acikPanel+" WHERE "+deleteWhere+" = "+miniId.getText()+";");
            miniFormu.dispatchEvent(new WindowEvent(miniFormu, WindowEvent.WINDOW_CLOSING));
            Main.reloadEverything();
        });

        miniEkle.addActionListener(e -> {
            if(miniId.getText().equals("")){
                try{
                    Main.update("INSERT INTO "+acikPanel+" VALUES (NULL, '"+miniAd.getText()+"');");
                }catch(Exception err){
                    System.out.println(err.getMessage());
                }
            }else{
                Main.update("UPDATE "+acikPanel+" SET "+setWhat+" = '"+miniAd.getText()+"' WHERE "+deleteWhere+" = "+miniId.getText()+";");
            }
            miniFormu.dispatchEvent(new WindowEvent(miniFormu, WindowEvent.WINDOW_CLOSING));
            Main.reloadEverything();
        });

        yeni_renk.addActionListener(e -> {
            miniId.setBounds(100, 10, 75, 30);
            miniCB.setBounds(185, 10, 75, 30);
            miniPaneli.add(miniId);
            miniId.setEditable(false);
            miniPaneli.add(miniCB);
            acikPanel="tbl_renk";
            setWhat="Renk";
            deleteWhere="RenkID";
            miniAd.setText("");
            miniPaneli.remove(renkLabel);
            miniPaneli.remove(renkler);
            miniPaneli.remove(sehirLabel);
            miniPaneli.remove(sehirler);
            miniPaneli.remove(vitesLabel);
            miniPaneli.remove(vitesler);
            miniPaneli.remove(yakitLabel);
            miniPaneli.remove(yakitlar);
            renkler.setSelectedIndex(0);
            sehirler.setSelectedIndex(0);
            vitesler.setSelectedIndex(0);
            yakitlar.setSelectedIndex(0);
            miniId.setText("1");
            idLabel.setBounds(10, 10, 80, 30);
            miniPaneli.add(secLabel);
            renkler.setBounds(100, 50, 150, 30);
            miniPaneli.add(renkler);
            miniCB.setSelected(false);
            miniEkle.setText("Güncelle");
            renkler.setEnabled(true);
            sehirler.setEnabled(true);
            vitesler.setEnabled(true);
            yakitlar.setEnabled(true);
            miniSil.setEnabled(true);
            renkLabel.setBounds(10, 90, 80, 30);
            miniPaneli.add(idLabel);
            miniFormu.setVisible(true);
            miniPaneli.add(renkLabel);
        });

        yeni_sehir.addActionListener(e -> {
            miniId.setBounds(100, 10, 75, 30);
            miniCB.setBounds(185, 10, 75, 30);
            miniPaneli.add(miniId);
            miniId.setEditable(false);
            miniPaneli.add(miniCB);
            acikPanel="tbl_sehir";
            setWhat="Sehir";
            deleteWhere="SehirID";
            miniAd.setText("");
            miniPaneli.remove(renkLabel);
            miniPaneli.remove(renkler);
            miniPaneli.remove(sehirLabel);
            miniPaneli.remove(sehirler);
            miniPaneli.remove(vitesLabel);
            miniPaneli.remove(vitesler);
            miniPaneli.remove(yakitLabel);
            miniPaneli.remove(yakitlar);
            renkler.setSelectedIndex(0);
            sehirler.setSelectedIndex(0);
            vitesler.setSelectedIndex(0);
            yakitlar.setSelectedIndex(0);
            miniId.setText("1");
            idLabel.setBounds(10, 10, 80, 30);
            miniPaneli.add(secLabel);
            sehirler.setBounds(100, 50, 150, 30);
            miniPaneli.add(sehirler);
            sehirLabel.setBounds(10, 90, 80, 30);
            miniPaneli.add(idLabel);
            miniFormu.setVisible(true);
            miniCB.setSelected(false);
            miniEkle.setText("Güncelle");
            renkler.setEnabled(true);
            sehirler.setEnabled(true);
            vitesler.setEnabled(true);
            yakitlar.setEnabled(true);
            miniSil.setEnabled(true);
            miniPaneli.add(sehirLabel);
        });

        yeni_yakit.addActionListener(e -> {
            miniId.setBounds(100, 10, 75, 30);
            miniCB.setBounds(185, 10, 75, 30);
            miniPaneli.add(miniId);
            miniId.setEditable(false);
            miniPaneli.add(miniCB);
            acikPanel="tbl_yakitturu";
            setWhat="Yakit_Turu";
            deleteWhere="YakitTuruID";
            miniAd.setText("");
            miniPaneli.remove(renkLabel);
            miniPaneli.remove(renkler);
            miniPaneli.remove(sehirLabel);
            miniPaneli.remove(sehirler);
            miniPaneli.remove(vitesLabel);
            miniPaneli.remove(vitesler);
            miniPaneli.remove(yakitLabel);
            miniPaneli.remove(yakitlar);
            renkler.setSelectedIndex(0);
            sehirler.setSelectedIndex(0);
            vitesler.setSelectedIndex(0);
            yakitlar.setSelectedIndex(0);
            miniCB.setSelected(false);
            miniEkle.setText("Güncelle");
            renkler.setEnabled(true);
            sehirler.setEnabled(true);
            vitesler.setEnabled(true);
            yakitlar.setEnabled(true);
            miniSil.setEnabled(true);
            miniId.setText("1");
            idLabel.setBounds(10, 10, 80, 30);
            miniPaneli.add(secLabel);
            yakitlar.setBounds(100, 50, 150, 30);
            miniPaneli.add(yakitlar);
            yakitLabel.setBounds(10, 90, 80, 30);
            miniPaneli.add(idLabel);
            miniFormu.setVisible(true);
            miniPaneli.add(yakitLabel);
        });

        yeni_vites.addActionListener(e -> {
            miniId.setBounds(100, 10, 75, 30);
            miniCB.setBounds(185, 10, 75, 30);
            miniPaneli.add(miniId);
            miniId.setEditable(false);
            miniPaneli.add(miniCB);
            acikPanel="tbl_vitesturu";
            setWhat="Vites_Turu";
            deleteWhere="VitesTuruID";
            miniAd.setText("");
            miniPaneli.remove(renkLabel);
            miniPaneli.remove(renkler);
            miniPaneli.remove(sehirLabel);
            miniPaneli.remove(sehirler);
            miniPaneli.remove(vitesLabel);
            miniPaneli.remove(vitesler);
            miniPaneli.remove(yakitLabel);
            miniPaneli.remove(yakitlar);
            miniCB.setSelected(false);
            miniEkle.setText("Güncelle");
            renkler.setEnabled(true);
            sehirler.setEnabled(true);
            vitesler.setEnabled(true);
            yakitlar.setEnabled(true);
            miniSil.setEnabled(true);
            renkler.setSelectedIndex(0);
            sehirler.setSelectedIndex(0);
            vitesler.setSelectedIndex(0);
            yakitlar.setSelectedIndex(0);
            miniId.setText("1");
            idLabel.setBounds(10, 10, 80, 30);
            miniPaneli.add(secLabel);
            vitesler.setBounds(100, 50, 150, 30);
            miniPaneli.add(vitesler);
            vitesLabel.setBounds(10, 90, 80, 30);
            miniPaneli.add(idLabel);
            miniFormu.setVisible(true);
            miniPaneli.add(vitesLabel);
        });




        // - ARABA F
        JFrame arabaFormu = new JFrame("Arabalar");
        arabaFormu.setSize(400, 360);
        arabaFormu.setResizable(false);
        arabaFormu.setLocationRelativeTo(frame);

        JPanel arabaPaneli = new JPanel();
        arabaPaneli.setLayout(null);
        arabaFormu.add(arabaPaneli);
        arabaPaneli.setBackground(arkaplan);

        arabaPaneli.add(arabaSil);
        arabaPaneli.add(arabaEkle);

        JTextField arabaMarka = new JTextField("", 20);
        arabaMarka.setBounds(100, 90, 280, 30);
        arabaPaneli.add(arabaMarka);

        JTextField arabaModel = new JTextField("", 20);
        arabaModel.setBounds(100, 130, 280, 30);
        arabaPaneli.add(arabaModel);

        arabaSil.addActionListener(e -> {
            try{
                Main.update("DELETE FROM "+acikPanel+" WHERE ArabaID = "+miniId.getText()+";");
                miniFormu.dispatchEvent(new WindowEvent(miniFormu, WindowEvent.WINDOW_CLOSING));
                Main.reloadEverything();
            }catch(Exception err){
                System.out.println(err.getMessage());
            }
        });

        arabaEkle.addActionListener(e -> {
            if(!(arabaMarka.getText().equals("") || arabaModel.getText().equals(""))){
                String s = findAraba(arabaMarka.getText(), arabaModel.getText());
                if(s.equals("")){
                    assert yakitlar.getSelectedItem() != null;
                    String yakit = ((ComboItem)yakitlar.getSelectedItem()).getKey()[0].toString();
                    assert vitesler.getSelectedItem() != null;
                    String vites = ((ComboItem)vitesler.getSelectedItem()).getKey()[0].toString();
                    assert renkler.getSelectedItem() != null;
                    String renk = ((ComboItem)renkler.getSelectedItem()).getKey()[0].toString();
                    if(miniId.getText().equals("")){
                        Main.addAraba(arabaMarka.getText(), arabaModel.getText(), renk, vites, yakit);
                    }else{
                        Main.update("UPDATE tbl_araba SET \n" +
                                "Araba_Marka = '"+arabaMarka.getText()+"', \n" +
                                "Araba_Model = '"+arabaModel.getText()+"', \n" +
                                "Araba_VitesTuruID = "+vites+", \n" +
                                "Araba_YakitTuruID = "+yakit+", \n" +
                                "Araba_RenkID = "+renk+"\n" +
                                "WHERE ArabaID = "+miniId.getText()+";");
                    }
                    arabaFormu.dispatchEvent(new WindowEvent(arabaFormu, WindowEvent.WINDOW_CLOSING));
                    Main.reloadEverything();
                }else{
                    System.out.println("Böyle bir araba zaten var!");
                }
            }
        });

        yeni_araba.addActionListener(e -> {
            renkler.setEnabled(true);
            sehirler.setEnabled(true);
            vitesler.setEnabled(true);
            yakitlar.setEnabled(true);
            arabaSil.setEnabled(true);
            arabaEkle.setText("Güncelle");
            arabaMarka.setText("");
            arabaModel.setText("");
            arabalarND.setSelectedIndex(0);
            renkler.setSelectedIndex(0);
            vitesler.setSelectedIndex(0);
            yakitlar.setSelectedIndex(0);
            arabalarND.setEnabled(true);
            miniId.setText("1");
            acikPanel="tbl_araba";
            idLabel.setBounds(10, 10, 80, 30);
            miniId.setBounds(100, 10, 75, 30);
            miniId.setEditable(false);
            miniCB.setSelected(false);
            miniCB.setBounds(185, 10, 75, 30);
            secLabel.setBounds(10, 50, 80, 30);
            markaLabel.setBounds(10, 90, 80, 30);
            modelLabel.setBounds(10, 130, 80, 30);
            renkLabel.setBounds(10, 170, 80, 30);
            vitesLabel.setBounds(10, 210, 80, 30);
            yakitLabel.setBounds(10, 250, 80, 30);
            arabaPaneli.add(markaLabel);
            arabaPaneli.add(modelLabel);
            arabaPaneli.add(secLabel);
            arabaPaneli.add(renkLabel);
            arabaPaneli.add(vitesLabel);
            arabaPaneli.add(yakitLabel);
            arabaPaneli.add(idLabel);

            arabaPaneli.add(miniId);
            arabaPaneli.add(miniCB);

            arabalarND.setBounds(100, 50, 280, 30);
            arabaPaneli.add(arabalarND);
            renkler.setBounds(100, 170, 280, 30);
            arabaPaneli.add(renkler);
            vitesler.setBounds(100, 210, 280, 30);
            arabaPaneli.add(vitesler);
            yakitlar.setBounds(100, 250, 280, 30);
            arabaPaneli.add(yakitlar);
            arabaFormu.setVisible(true);
        });


        // - YENİ KAYIT FORMU - \\
        yeniKayitFormu.setSize(270, 440);
        yeniKayitFormu.setResizable(false);
        yeniKayitFormu.setLocationRelativeTo(frame);

        JPanel kayitPaneli = new JPanel();
        kayitPaneli.setLayout(null);
        yeniKayitFormu.add(kayitPaneli);
        kayitPaneli.setBackground(arkaplan);

        JTextField ad = new JTextField("", 20);
        ad.setBounds(100, 10, 150, 30);
        kayitPaneli.add(ad);



        JTextField km = new JTextField("", 20);
        km.setBounds(100, 250, 150, 30);
        kayitPaneli.add(km);
        JTextField fiyat = new JTextField("", 20);
        fiyat.setBounds(100, 290, 150, 30);
        kayitPaneli.add(fiyat);

        hataLabeli.setBounds(10, 330, 240, 30);
        kayitPaneli.add(hataLabeli);

        JButton ekle = new JButton("Ekle");
        ekle.setBounds(10, 370, 240, 30);
        kayitPaneli.add(ekle);

        // - EKLE BUTONU - \\
        ekle.addActionListener(ekleEvent -> {
            if(ad.getText().equals("")){
                giveError("noName");
            }else if(km.getText().equals("")){
                giveError("noKm");
            }else if(fiyat.getText().equals("")){
                giveError("noPrice");
            }else{
                System.out.println("Ekleniyor...");
                assert renkler.getSelectedItem() != null;
                String renk = ((ComboItem)renkler.getSelectedItem()).getKey()[0].toString();
                assert sehirler.getSelectedItem() != null;
                String sehir = ((ComboItem)sehirler.getSelectedItem()).getKey()[0].toString();
                assert vitesler.getSelectedItem() != null;
                String vites = ((ComboItem)vitesler.getSelectedItem()).getKey()[0].toString();
                assert yakitlar.getSelectedItem() != null;
                String yakit = ((ComboItem)yakitlar.getSelectedItem()).getKey()[0].toString();
                assert arabalar.getSelectedItem() != null;
                String marka = ((ComboItem)arabalar.getSelectedItem()).getKey()[0].toString();
                String model = ((ComboItem)arabalar.getSelectedItem()).getKey()[1].toString();
                Main.addIlan(ad.getText(), sehir, marka, model, renk, vites, yakit, km.getText(), fiyat.getText());
            }
        });

        yeni_kayit.addActionListener(kayitEvent -> {
            idLabel.setBounds(10, 10, 80, 30);
            adLabel.setBounds(10, 10, 80, 30);
            arabaLabel.setBounds(10, 50, 80, 30);
            secLabel.setBounds(10, 50, 80, 30);
            renkLabel.setBounds(10, 90, 80, 30);
            sehirLabel.setBounds(10, 130, 80, 30);
            vitesLabel.setBounds(10, 170, 80, 30);
            yakitLabel.setBounds(10, 210, 80, 30);
            kmLabel.setBounds(10, 250, 80, 30);
            fiyatLabel.setBounds(10, 290, 80, 30);
            kayitPaneli.add(adLabel);
            kayitPaneli.add(arabaLabel);
            kayitPaneli.add(renkLabel);
            kayitPaneli.add(sehirLabel);
            kayitPaneli.add(vitesLabel);
            kayitPaneli.add(yakitLabel);
            kayitPaneli.add(kmLabel);
            kayitPaneli.add(fiyatLabel);

            arabalar.setBounds(100, 50, 150, 30);
            kayitPaneli.add(arabalar);
            renkler.setBounds(100, 90, 150, 30);
            kayitPaneli.add(renkler);
            sehirler.setBounds(100, 130, 150, 30);
            kayitPaneli.add(sehirler);
            vitesler.setBounds(100, 170, 150, 30);
            kayitPaneli.add(vitesler);
            yakitlar.setBounds(100, 210, 150, 30);
            kayitPaneli.add(yakitlar);
            yeniKayitFormu.setVisible(true);
            clearForms(ad,fiyat,km);
        });


        // - FİLTRELEME FORMU - \\
        JFrame filtrelemeFormu = new JFrame("Filtrele");
        filtrelemeFormu.setSize(310, 490);
        filtrelemeFormu.setResizable(false);
        filtrelemeFormu.setLocationRelativeTo(frame);

        JPanel filtrePaneli = new JPanel();
        filtrePaneli.setLayout(null);
        filtrelemeFormu.add(filtrePaneli);
        filtrePaneli.setBackground(arkaplan);

        JCheckBox filtreAd = new JCheckBox("İlan Adı:");
        filtreAd.setBounds(10, 10, 120, 30);
        filtrePaneli.add(filtreAd);

        JCheckBox filtreTam = new JCheckBox("Sadece tam eşleşmeleri getir.");
        filtreTam.setBounds(50, 50, 250, 30);
        filtrePaneli.add(filtreTam);

        JCheckBox filtreFiyat = new JCheckBox("Fiyat Aralığı:");
        filtreFiyat.setBounds(10, 90, 120, 30);
        filtrePaneli.add(filtreFiyat);

        JCheckBox filtreKm = new JCheckBox("KM Aralığı:");
        filtreKm.setBounds(10, 130, 120, 30);
        filtrePaneli.add(filtreKm);

        JCheckBox filtreTarih = new JCheckBox("İlan Tarihi:");
        filtreTarih.setBounds(10, 170, 120, 30);
        filtrePaneli.add(filtreTarih);

        JCheckBox filtreSehir = new JCheckBox("Şehir:");
        filtreSehir.setBounds(10, 210, 120, 30);
        filtrePaneli.add(filtreSehir);

        JCheckBox filtreYakit = new JCheckBox("Yakıt Türü:");
        filtreYakit.setBounds(10, 250, 120, 30);
        filtrePaneli.add(filtreYakit);

        JCheckBox filtreVites = new JCheckBox("Vites Türü:");
        filtreVites.setBounds(10, 290, 120, 30);
        filtrePaneli.add(filtreVites);

        JCheckBox filtreRenk = new JCheckBox("Renk:");
        filtreRenk.setBounds(10, 330, 120, 30);
        filtrePaneli.add(filtreRenk);

        JCheckBox filtreSirala = new JCheckBox("Sırala:");
        filtreSirala.setBounds(10, 370, 120, 30);
        filtrePaneli.add(filtreSirala);

        JTextField filtreAdField = new JTextField("", 20);
        filtreAdField.setBounds(140, 10, 150, 30);
        filtrePaneli.add(filtreAdField);

        JTextField filtreFiyatMinField = new JTextField("", 20);
        filtreFiyatMinField.setBounds(140, 90, 60, 30);
        filtrePaneli.add(filtreFiyatMinField);

        JLabel fiyatTire = new JLabel("-", JTextField.CENTER);
        fiyatTire.setBounds(200, 90, 30, 30);
        filtrePaneli.add(fiyatTire);

        JTextField filtreFiyatMaxField = new JTextField("", 20);
        filtreFiyatMaxField.setBounds(230, 90, 60, 30);
        filtrePaneli.add(filtreFiyatMaxField);

        JTextField filtreKmMinField = new JTextField("", 20);
        filtreKmMinField.setBounds(140, 130, 60, 30);
        filtrePaneli.add(filtreKmMinField);

        JLabel fiyatTire2 = new JLabel("-", JTextField.CENTER);
        fiyatTire2.setBounds(200, 130, 30, 30);
        filtrePaneli.add(fiyatTire2);

        JTextField filtreKmMaxField = new JTextField("", 20);
        filtreKmMaxField.setBounds(230, 130, 60, 30);
        filtrePaneli.add(filtreKmMaxField);

        JComboBox<ComboItem> filtreTarihCB = new JComboBox<>();
        filtreTarihCB.addItem(new ComboItem("1 HOUR","Son 1 saat"));
        filtreTarihCB.addItem(new ComboItem("1 DAY","Son 24 saat"));
        filtreTarihCB.addItem(new ComboItem("1 WEEK","Son 1 hafta"));
        filtreTarihCB.addItem(new ComboItem("1 MONTH","Son 1 ay"));
        filtreTarihCB.addItem(new ComboItem("1 YEAR","Son 1 yıl"));
        filtreTarihCB.setBounds(140, 170, 150, 30);
        filtrePaneli.add(filtreTarihCB);


        JComboBox<ComboItem> filtreSiralaCB = new JComboBox<>();
        filtreSiralaCB.addItem(new ComboItem("ASC","A'dan Z'ye"));
        filtreSiralaCB.addItem(new ComboItem("DESC","Z'den A'ya"));
        filtreSiralaCB.setBounds(140, 370, 150, 30);
        filtrePaneli.add(filtreSiralaCB);

        JButton goFilter = new JButton("Filtrele");
        goFilter.setBounds(10, 410, 280, 30);
        filtrePaneli.add(goFilter);

        //renkler.setBounds(100, 10, 100, 30);
        //filtrePaneli.add(renkler);

        filtrele.addActionListener(filtreEvent -> {
            sehirler.setBounds(140, 210, 150, 30);
            yakitlar.setBounds(140, 250, 150, 30);
            vitesler.setBounds(140, 290, 150, 30);
            renkler.setBounds(140, 330, 150, 30);
            filtrePaneli.add(sehirler);
            filtrePaneli.add(yakitlar);
            filtrePaneli.add(vitesler);
            filtrePaneli.add(renkler);
            filtrelemeFormu.setVisible(true);
            clearForms(ad,fiyat,km);
        });

        goFilter.addActionListener(e -> {
            String queryString = "SELECT *\n" +
                    "FROM tbl_ilan\n" +
                    "INNER JOIN tbl_araba ON tbl_ilan.Ilan_ArabaID = tbl_araba.ArabaID\n" +
                    "INNER JOIN tbl_sehir ON tbl_ilan.Ilan_SehirID = tbl_sehir.SehirID\n" +
                    "INNER JOIN tbl_renk ON tbl_araba.Araba_RenkID = tbl_renk.RenkID\n" +
                    "INNER JOIN tbl_vitesturu ON tbl_araba.Araba_VitesTuruID = tbl_vitesturu.VitesTuruID\n" +
                    "INNER JOIN tbl_yakitturu ON tbl_araba.Araba_YakitTuruID = tbl_yakitturu.YakitTuruID\n" +
                    "WHERE 1=1\n";

            if(filtreAd.isSelected()){
                if(filtreTam.isSelected()){
                    queryString += "AND tbl_ilan.Ilan_Adi = '"+filtreAdField.getText()+"'\n";
                }else{
                    queryString += "AND tbl_ilan.Ilan_Adi LIKE '%"+filtreAdField.getText()+"%'\n";
                }
            }

            if(filtreFiyat.isSelected()){
                if(!filtreFiyatMinField.getText().equals("")){
                    queryString += "AND tbl_ilan.Ilan_Fiyat >= "+filtreFiyatMinField.getText()+"\n";
                }
                if(!filtreFiyatMaxField.getText().equals("")){
                    queryString += "AND tbl_ilan.Ilan_Fiyat < "+filtreFiyatMaxField.getText()+"\n";
                }
            }

            if(filtreKm.isSelected()){
                if(!filtreKmMinField.getText().equals("")){
                    queryString += "AND tbl_ilan.Ilan_Km >= "+filtreKmMinField.getText()+"\n";
                }
                if(!filtreKmMaxField.getText().equals("")){
                    queryString += "AND tbl_ilan.Ilan_Km < "+filtreKmMaxField.getText()+"\n";
                }
            }

            if(filtreTarih.isSelected()){
                assert filtreTarihCB.getSelectedItem() != null;
                queryString += "AND tbl_ilan.Ilan_Tarih > DATE_SUB(CURDATE(), INTERVAL "+
                        ((ComboItem)filtreTarihCB.getSelectedItem()).getKey()[0].toString()
                        +")\n";
            }

            if(filtreSehir.isSelected()){
                assert sehirler.getSelectedItem() != null;
                queryString += "AND tbl_sehir.SehirID = "+
                        ((ComboItem)sehirler.getSelectedItem()).getKey()[0].toString()
                        +"\n";
            }

            if(filtreYakit.isSelected()){
                assert yakitlar.getSelectedItem() != null;
                queryString += "AND tbl_yakitturu.YakitTuruID = "+
                        ((ComboItem)yakitlar.getSelectedItem()).getKey()[0].toString()
                        +"\n";
            }

            if(filtreVites.isSelected()){
                assert vitesler.getSelectedItem() != null;
                queryString += "AND tbl_vitesturu.VitesTuruID = "+
                        ((ComboItem)vitesler.getSelectedItem()).getKey()[0].toString()
                        +"\n";
            }

            if(filtreRenk.isSelected()){
                assert renkler.getSelectedItem() != null;
                queryString += "AND tbl_araba.Araba_RenkID = "+
                        ((ComboItem)renkler.getSelectedItem()).getKey()[0].toString()
                        +"\n";
            }

            if(filtreSirala.isSelected()){
                assert filtreSiralaCB.getSelectedItem() != null;
                queryString += "ORDER BY Ilan_Adi "+
                        ((ComboItem)filtreSiralaCB.getSelectedItem()).getKey()[0].toString()
                        +"\n";
            }

            if(!filtreAd.isSelected() && !filtreFiyat.isSelected() && !filtreKm.isSelected() &&
                    !filtreTarih.isSelected() && !filtreSehir.isSelected() && !filtreYakit.isSelected() &&
                    !filtreVites.isSelected() && !filtreRenk.isSelected() && !filtreSirala.isSelected()){
                System.out.println("Filtre kapalı");
                Main.getAllIlan();
            }else{
                Main.filterQuery(queryString);
            }
        });


        // - SEÇİLİ SATIRI SİL - \\
        sil.addActionListener(e -> {
            String ID = tablo.getValueAt(tablo.getSelectedRow(),0).toString();
            Main.deleteIlan(ID);
        });

        // - YENİLE - \\
        yenile.addActionListener(yenileEvent -> {
            if(Main.isNoFilter()){
                Main.getAllIlan();
            }else{
                Main.repeatFilter();
            }
        });

    }

    static class LinkedJTable extends JTable{

        LinkedJTable(){
            addPropertyChangeListener(evt -> {
                if ("tableCellEditor".equals(evt.getPropertyName())) {
                    if (!isEditing()){
                        processEditingStopped();
                    }
                }
            });
        }
        private void processEditingStopped() {
            Main.updateCell(editingRow, editingColumn);
        }
    }

    static class ComboItem {
        private Object key;
        private Object[] keyArray;
        private String value;
        private Boolean isArray = false;

        ComboItem(Object key,String value) {
            this.key = key;
            this.value = value;
        }

        ComboItem(Object key[],String value) {
            this.isArray = true;
            this.keyArray = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        Object[] getKey() {
            if(isArray){
                return keyArray;
            }else{
                return new Object[]{key};
            }
        }
    }
}