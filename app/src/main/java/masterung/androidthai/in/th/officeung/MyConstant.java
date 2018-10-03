package masterung.androidthai.in.th.officeung;

public class MyConstant {

    private String hostString = "ftp.swiftcodingthai.com";
    private String userFTPString = "ann@swiftcodingthai.com";
    private String passwordString = "Abc12345";
    private int portAnInt = 21;

    private String urlAddUserString = "http://swiftcodingthai.com/ann/addUserMasterAndroid.php";
    private String prefixString = "http://swiftcodingthai.com/ann/MasterUNG";
    private String urlGetAllUserString = "http://swiftcodingthai.com/ann/getAllUser.php";

    public String getUrlGetAllUserString() {
        return urlGetAllUserString;
    }

    public String getPrefixString() {
        return prefixString;
    }

    public String getUrlAddUserString() {
        return urlAddUserString;
    }

    public String getHostString() {
        return hostString;
    }

    public String getUserFTPString() {
        return userFTPString;
    }

    public String getPasswordString() {
        return passwordString;
    }

    public int getPortAnInt() {
        return portAnInt;
    }
}
