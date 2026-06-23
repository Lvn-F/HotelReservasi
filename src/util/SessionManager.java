package util;

public class SessionManager {
    
    private static String username;
    private static String nama;
    private static String jabatan;

    public static String getUsername() { return username; }
    public static String getNama() { return nama; }
    public static String getJabatan() { return jabatan; }

    public static void setUsername(String u) { username = u; }
    public static void setNama(String n) { nama = n; }
    public static void setJabatan(String j) { jabatan = j; }

    public static void clear() {
        username = null;
        nama = null;
        jabatan = null;
    }
}