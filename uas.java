import java.io.*;
import java.util.*;

public class uas {
    public static BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));

    public static final int MIN_SALDO = 50000;
    public static final int MIN_SALDO_AWAL = 500000;

    // declare Data Bank
    public static Vector data_AccType = new Vector<String>(); // data Tipe Rekening
    public static Vector data_MrgState = new Vector<String>(); // data Perkawinan
    public static Vector data_DwellType = new Vector<String>(); // data Tempat tinggal
    public static Vector data_LastSchool = new Vector<String>(); // data Pendidikan Terakhir
    public static Vector data_Job = new Vector<String>(); // data Pekerjaan

    // declare inputan untuk nasabah baru
    public static String nik, phone, name, accessCode, temp, accNumStr;
    public static Integer accountNum, pin, accType, mrgState, dwellType, lastSchool, job, temp1, tempBal;

    // declare Data Vector nasabah yang dibutuhkan
    public static Vector clientNIK = new Vector<String>(); // NIK nasabah
    public static Vector clientName = new Vector<String>(); // nama nasabah
    public static Vector clientPhone = new Vector<String>(); // no telp nasabah
    public static Vector clientAccType = new Vector<Integer>(); // index tipe rekening nasabah
    public static Vector clientMrgState = new Vector<Integer>(); // index status pernikahan
    public static Vector clientDwellType = new Vector<Integer>(); // index tipe tempat tinggal nasabah
    public static Vector clientLastSchool = new Vector<Integer>(); // index pendidikan terakhir nasabah
    public static Vector clientJob = new Vector<Integer>(); // index tipe pekerjaan
    public static Vector clientAccountNum = new Vector<String>(); // no rekening
    public static Vector clientAccessCode = new Vector<String>(); // kode akses (username nasabah)
    public static Vector clientPIN = new Vector<String>(); // pin nasabah
    public static Vector clientBalance = new Vector<Integer>(); // saldo nasabah
    public static Vector clientAvl = new Vector<Boolean>(); // buka/tutup buku nasabah

    // declare Data Vector nasabah untuk cetak buku
    public static Vector indHistory = new Vector<String>(); // catat kode akses dari nasabah
    public static Vector history = new Vector<String>(); // catat history dari nasabah

    // declare Data Vector admin
    public static Vector adminUsername = new Vector<String>(); // username admin
    public static Vector adminPassword = new Vector<String>(); // password admin

    // declare inputan untuk create user admin
    public static String adminUser, adminPass;

    public static String username, password, destAccount;
    public static int menu, nominal;
    public static Boolean edit;
    // ket:
    // username = input username pada login
    // password = input password pada login
    // destAccount = input no.rek tujuan pada transfer
    // menu = input pada menu
    // nominal = jumlah setor/tarik/transfer

    static void garisBatas() {
        for (int i = 0; i < 26; i++) {
            System.out.print("=");
        }
        System.out.println();
    }

    static void login() throws IOException {
        do {
            System.out.println("===== SELAMAT DATANG =====\n      DI BANK SEGURO");
            garisBatas();
            System.out.println("\tMENU AWAL\n\tLogin as\n1. Admin\n2. User\n3. Exit");
            garisBatas();
            System.out.print("Masukkan index menu yang diinginkan: ");
            menu = Integer.parseInt(rd.readLine());
            garisBatas();
            switch (menu) {
                case 1:
                    System.out.print("Masukkan username : ");
                    username = rd.readLine();
                    System.out.print("Masukkan password : ");
                    password = rd.readLine();
                    loginProcess(menu);
                    break;

                case 2:
                    loginProcess(menu);
                    break;

                case 3:
                    System.out.println("Exit...");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Maaf, index menu yang Anda pilih tidak ada");
                    break;
            }
        } while (true);
    }

    static void loginProcess(int parameter) throws IOException {
        if (parameter == 1) { // untuk admin
            // cek kesesuaian username dan password
            if (adminUsername.contains(username) == true && adminPassword.contains(password) == true
                    && password.equals(adminPassword.get(adminUsername.indexOf(username))) == true) {
                menuAdmin();
            } else {
                if (adminUsername.contains(username) != true) {
                    System.out.println("Maaf, username salah");
                } else if (adminPassword.contains(password) != true) {
                    System.out.println("Maaf, password salah");
                }
                login();
            }
        } else if (parameter == 2) { // untuk user
            System.out.println("\tLogin as\n1. User\n2. Buka Rekening Baru\n3. Keluar");
            garisBatas();
            System.out.print("Masukkan index menu yang diinginkan: ");
            menu = Integer.parseInt(rd.readLine());
            garisBatas();
            switch (menu) {
                case 1: // untuk pengguna lama (login ke dalam akun)
                    System.out.print("Masukkan kode akses: ");
                    username = rd.readLine();
                    System.out.print("Masukkan PIN: ");
                    password = rd.readLine();

                    // cek kesesuaian username dan password
                    if (clientAccessCode.contains(username) == true && clientPIN.contains(password) == true
                            && password.equals(clientPIN.get(clientAccessCode.indexOf(username))) == true
                            && (Boolean) (clientAvl.get(clientAccessCode.indexOf(username))) == true) {
                        menuUser();
                        // cek apakah no rek masih tersedia atau telah ditutup
                        if ((Boolean) clientAvl.elementAt(clientAccessCode.indexOf(username)) != true) {
                            System.out.println("Maaf, rekening Anda telah ditutup");
                            login();
                        }
                    } else {
                        if (clientAccessCode.contains(username) != true) {
                            System.out.println("Maaf, kode akses salah");
                        } else if (clientPIN.contains(password) != true) {
                            System.out.println("Maaf, PIN salah");
                        } else {
                            System.out.println("Maaf, kode akses/PIN tidak sesuai");
                        }
                        login();
                    }
                    break;

                case 2: // untuk pengguna baru dan membuat rekening baru mereka
                    registrasiBaru1(0);
                    break;

                case 3:
                    login();
                    break;

                default:
                    System.out.println("Maaf, index yang Anda masukkan tidak tersedia");
                    break;
            }
        }
    }

    static void menuAdmin() throws IOException {
        Boolean loop = true;
        do {
            garisBatas();
            System.out.println("===== ADMIN =====");
            System.out
                    .println(
                            "1. Tampilkan semua data nasabah\n2. Cari data nasabah\n3. Edit/hapus data nasabah\n4. Hapus Data Rekening yang Ditutup\n5. Tambah User Admin\n6. Ubah username/password admin\n7. Keluar");
            garisBatas();
            System.out.print("Masukkan index menu yang diinginkan: ");
            menu = Integer.parseInt(rd.readLine());
            garisBatas();
            switch (menu) {
                case 1: // tampilkan semua data nasabah
                    for (int i = 0; i < clientAccountNum.size(); i++) {
                        printDataNasabah(i);
                        garisBatas();
                    }
                    break;

                case 2: // cari data nasabah
                    edit = false;
                    cariDataNasabah();
                    break;

                case 3: // edit dan hapus data nasabah
                    edit = true;
                    System.out.println(
                            "Silahkan cari data nasabah yang spesifik dengan menggunakan NIK, No. Rek., atau No. Telp");
                    garisBatas();
                    cariDataNasabah();
                    break;

                case 4: // hapus data rekening yang telah ditutup
                    System.out.println("1. Ya\n2. Tidak");
                    System.out.print("Apakah Anda yakin untuk menghapus rekening yang telah ditutup? : ");
                    menu = Integer.parseInt(rd.readLine());
                    if (menu == 1) {
                        for (int i = 0; i < clientAvl.size(); i++) {
                            if ((Boolean) (clientAvl.get(i)) == false) {
                                engineDelete(i);
                            }
                        }
                    }
                    System.out.println("Data Telah Terhapus!!");
                    break;

                case 5: // tambah user admin
                    System.out.print("Masukkan username admin yang baru: ");
                    adminUser = rd.readLine();
                    System.out.print("Masukkan password admin yang baru: ");
                    adminPass = rd.readLine();
                    garisBatas();
                    adminUsername.add(adminUser);
                    adminPassword.add(adminPass);
                    System.out.println("Penambahan User Admin Berhasil !!");
                    menuAdmin();
                    break;

                case 6: // ubah username atau password admin saat ini
                    System.out.println("1. Username\n2. Password\n3. Batal");
                    System.out.print("Pilih apa yang akan dirubah: ");
                    menu = Integer.parseInt(rd.readLine());
                    if (menu == 1) {
                        System.out.print("Masukkan username admin yang baru: ");
                        adminUser = rd.readLine();
                        adminUsername.set((int) adminUsername.indexOf(username), adminUser);
                        System.out.println("Username Berhasil Diubah !");
                        System.out.println("Silahkan login kembali");
                        login();
                    } else if (menu == 2) {
                        System.out.print("Masukkan password admin yang baru: ");
                        adminPass = rd.readLine();
                        adminPassword.set((int) adminPassword.indexOf(password), adminPass);
                        System.out.println("Password Berhasil Diubah !");
                        System.out.println("Silahkan login kembali");
                        login();
                    } else {
                        menuAdmin();
                    }
                    break;

                case 7: // keluar
                    System.out.println("Log out...");
                    login();
                    break;

                default:
                    System.out.println("Maaf, index yang Anda tuju tidak ada");
                    break;
            }
        } while (loop == true);
    }

    static void registrasiBaru1(int parameter) throws IOException {
        switch (parameter) {
            case 0:
                System.out.println("==== Registrasi  Baru ====");
                registrasiBaru1(parameter + 1);
                break;

            case 1:
                garisBatas();
                System.out.print("Masukkan NIK (16 digit): ");
                nik = rd.readLine();
                if (nik.length() != 16) {
                    System.out.println("Maaf, NIK Anda masih kurang memenuhi syarat");
                    registrasiBaru1(parameter);
                } else if (clientNIK.contains(nik) == true) {
                    System.out.println("Maaf, NIK yang Anda masukkan telah digunakan");
                    System.out.println("Silahkan masukkan NIK lainnya");
                    registrasiBaru1(parameter);
                } else {
                    registrasiBaru1(parameter + 1);
                }
                break;

            case 2:
                garisBatas();
                System.out.print("Masukkan Nama (min 3 huruf): ");
                name = rd.readLine();
                if (name.length() < 3) {
                    System.out.println("Maaf, nama Anda masih tidak memenuhi syarat");
                    registrasiBaru1(parameter);
                } else {
                    registrasiBaru1(parameter + 1);
                }
                break;

            case 3:
                garisBatas();
                System.out.print("Masukkan No. Telp/HP (min 10 digit & max 13 digit): ");
                phone = rd.readLine();
                if (phone.length() < 10 || phone.length() > 13) {
                    System.out.println("Maaf, no Telp/HP Anda masih kurang memenuhi syarat");
                    registrasiBaru1(parameter);
                } else if (clientPhone.contains(phone) == true) {
                    System.out.println("Maaf, no. Telp yang Anda masukkan telah digunakan");
                    System.out.println("Silahkan masukkan no. Telp lainnya");
                    registrasiBaru1(parameter);
                } else {
                    registrasiBaru1(parameter + 1);
                }
                break;

            case 4:
                garisBatas();
                System.out.println("Pilih tipe rekening");
                for (int i = 0; i < data_AccType.size(); i++) {
                    System.out.println((i + 1) + ". " + data_AccType.elementAt(i));
                }
                garisBatas();
                System.out.print("Masukkan index dari tipe rekening yang dipilih: ");
                accType = (Integer.parseInt(rd.readLine())) - 1;
                if (accType >= data_AccType.size()) {
                    System.out.println("Maaf, tipe rekening yang Anda pilih tidak ada");
                    System.out.println("Silahkan pilih kembali tipe rekening Anda");
                    registrasiBaru1(parameter);
                } else {
                    registrasiBaru1(parameter + 1);
                }
                break;

            case 5:
                garisBatas();
                System.out.println("Pilih status perkawinan");
                for (int i = 0; i < data_MrgState.size(); i++) {
                    System.out.println((i + 1) + ". " + data_MrgState.elementAt(i));
                }
                garisBatas();
                System.out.print("Masukkan index dari status perkawinan yang dipilih: ");
                mrgState = (Integer.parseInt(rd.readLine())) - 1;
                if (mrgState >= data_MrgState.size()) {
                    System.out.println("Maaf, status perkawinan yang Anda pilih tidak ada");
                    System.out.println("Silahkan pilih kembali status perkawinan Anda");
                    registrasiBaru1(parameter);
                } else {
                    registrasiBaru1(parameter + 1);
                }
                break;

            case 6:
                garisBatas();
                System.out.println("Pilih tipe tempat tinggal");
                for (int i = 0; i < data_DwellType.size(); i++) {
                    System.out.println((i + 1) + ". " + data_DwellType.elementAt(i));
                }
                garisBatas();
                System.out.print("Masukkan index dari tipe tempat tinggal Anda: ");
                dwellType = (Integer.parseInt(rd.readLine())) - 1;
                if (dwellType >= data_DwellType.size()) {
                    System.out.println("Maaf, tipe tempat tinggal yang Anda pilih tidak ada");
                    System.out.println("Silahkan pilih kembali tipe tempat tinggal Anda");
                    registrasiBaru1(parameter);
                } else {
                    registrasiBaru1(parameter + 1);
                }
                break;

            case 7:
                garisBatas();
                System.out.println("Pilih pendidikan terakhir");
                for (int i = 0; i < data_LastSchool.size(); i++) {
                    System.out.println((i + 1) + ". " + data_LastSchool.elementAt(i));
                }
                garisBatas();
                System.out.print("Masukkan index dari pendidikan terakhir Anda: ");
                lastSchool = (Integer.parseInt(rd.readLine())) - 1;
                if (lastSchool >= data_LastSchool.size()) {
                    System.out.println("Maaf, pendidikan terakhir yang Anda pilih tidak ada");
                    System.out.println("Silahkan pilih kembali pendidikan terakhir Anda");
                    registrasiBaru1(parameter);
                } else {
                    registrasiBaru1(parameter + 1);
                }
                break;

            case 8:
                garisBatas();
                System.out.println("Pilih pekerjaan");
                for (int i = 0; i < data_Job.size(); i++) {
                    System.out.println((i + 1) + ". " + data_Job.elementAt(i));
                }
                garisBatas();
                System.out.print("Masukkan index dari pekerjaan Anda: ");
                job = (Integer.parseInt(rd.readLine())) - 1;
                if (job >= data_Job.size()) {
                    System.out.println("Maaf, pekerjaan yang Anda pilih tidak ada");
                    System.out.println("Silahkan pilih kembali pekerjaan Anda");
                    registrasiBaru1(parameter);
                } else {
                    registrasiBaru1(parameter + 1);
                }
                break;

            case 9:
                garisBatas();
                System.out.println("Kode Akses berfungsi sebagai username ketika Anda login");
                System.out.println("Kode akses dapat berisi huruf dan angka yang terdiri dari 6 karakter");
                garisBatas();
                System.out.print("Silahkan masukkan kode akses Anda: ");
                accessCode = rd.readLine();
                if (accessCode.length() != 6) {
                    System.out.println("Maaf, kode akses Anda tidak memenuhi syarat");
                    System.out.println("Silahkan masukkan kode akses kembali");
                    registrasiBaru1(parameter);
                } else if (clientAccessCode.contains(accessCode) == true) {
                    System.out.println("Maaf, kode akses yang Anda masukkan telah digunakan");
                    System.out.println("Silahkan masukkan kode akses kembali");
                    registrasiBaru1(parameter);
                } else {
                    System.out.println("Kode akses berhasil disimpan");
                    registrasiBaru1(parameter + 1);
                }
                break;

            case 10:
                garisBatas();
                System.out.println("PIN berfungsi sebagai password ketika Anda login");
                System.out.println("PIN terdiri dari 6 kombinasi angka");
                garisBatas();
                System.out.print("Silahkan masukkan PIN Anda: ");
                pin = Integer.parseInt(rd.readLine());
                temp = String.valueOf(pin);
                if (temp.length() != 6) {
                    System.out.println("Maaf, PIN yang Anda masukkan tidak memenuhi syarat");
                    System.out.println("Silahkan masukkan kembali PIN Anda");
                    registrasiBaru1(parameter);
                } else {
                    registrasiBaru1(parameter + 1);
                }
                break;

            case 11:
                accountNum = (int) (Math.random() * Math.pow(10, 10));
                if (clientAccountNum.contains(accountNum.toString())) {
                    registrasiBaru1(parameter);
                } else {
                    registrasiBaru1(parameter + 1);
                }
                break;

            case 12: // preview dari data-data yang telah dimasukkan oleh nasabah baru
                garisBatas();
                System.out.println("=== Data Nasabah Baru ===");
                System.out.println("No. Rek\t\t\t: " + accountNum);
                System.out.println("NIK\t\t\t: " + nik);
                System.out.println("Nama\t\t\t: " + name);
                System.out.println("No. Telp\t\t: " + phone);
                System.out.println("Account Type\t\t: " + data_AccType.get(accType));
                System.out.println("Status Perkawinan\t: " + data_MrgState.get(mrgState));
                System.out.println("Tipe Tempat Tinggal\t: " + data_DwellType.get(dwellType));
                System.out.println("Pendidikan Terakhir\t: " + data_LastSchool.get(lastSchool));
                System.out.println("Pekerjaan\t\t: " + data_Job.get(job));
                registrasiBaru1(parameter + 1);
                break;

            case 13:
                garisBatas();
                clientAccountNum.add(accountNum);
                clientAccessCode.add(accessCode);
                clientPIN.add(temp = String.valueOf(pin));
                clientNIK.add(nik);
                clientName.add(name);
                clientPhone.add(phone);
                clientAccType.add(accType);
                clientMrgState.add(mrgState);
                clientDwellType.add(dwellType);
                clientLastSchool.add(lastSchool);
                clientJob.add(job);
                clientBalance.add(MIN_SALDO_AWAL);
                clientAvl.add(true);
                indHistory.add(accessCode);
                history.add("Pembukaan Rekening Baru");
                System.out.println("Pendaftaran Nasabah Baru Berhasil");
                System.out.println("Silahkan login menggunakan kode akses dan PIN yang telah Anda buat");
                garisBatas();
                login();
                break;
        }
    }

    static void printDataNasabah(int index) {
        garisBatas();
        System.out.println("===== Data  Nasabah =====");
        System.out.println("No. Rek\t\t\t: " + clientAccountNum.elementAt(index));
        System.out.println("NIK\t\t\t: " + clientNIK.elementAt(index));
        System.out.println("Nama\t\t\t: " + clientName.elementAt(index));
        System.out.println("No. Telp\t\t: " + clientPhone.elementAt(index));
        System.out.println("Account Type\t\t: " + data_AccType.get((int) clientAccType.elementAt(index)));
        System.out.println("Status Perkawinan\t: " + data_MrgState.get((int) clientMrgState.elementAt(index)));
        System.out.println("Tipe Tempat Tinggal\t: " + data_DwellType.get((int) clientDwellType.elementAt(index)));
        System.out.println("Pendidikan Terakhir\t: " + data_LastSchool.get((int) clientLastSchool.elementAt(index)));
        System.out.println("Pekerjaan\t\t: " + data_Job.get((int) clientJob.elementAt(index)));
        System.out.print("\nStatus\t\t\t: ");
        if ((Boolean) (clientAvl.get(index)) == true) {
            System.out.print("Rekening Aktif\n");
        } else {
            System.out.print("Rekening Ditutup\n");
        }
    }

    static void cariDataNasabah() throws IOException {
        System.out.println("Cari Data Nasabah Sesuai Dengan: ");
        System.out.println(
                "1. No. Rekening\n2. NIK\n3. Nama\n4. No. Telp\n5. Tipe Rekening\n6. Status Pernikahan\n7. Tipe Tempat Tinggal\n8. Pendidikan Terakhir\n9. Pekerjaan\n10. Kembali ke menu awal");
        garisBatas();
        System.out.print("Masukkan index menu yang diinginkan: ");
        menu = Integer.parseInt(rd.readLine());
        garisBatas();
        prosesCariDataNasabah(menu);
    }

    static void advanceSearch() throws IOException {
        garisBatas();
        System.out.println("Tindakan Selanjutnya: ");
        System.out.println("1. Edit Data Nasabah\n2. Hapus Data Nasabah\n3. Kembali ke menu");
        garisBatas();
        System.out.print("Masukkan index menu yang diinginkan: ");
        menu = Integer.parseInt(rd.readLine());
        garisBatas();
        switch (menu) {
            case 1: // edit data nasabah
                System.out.println("======= Ubah  Data =======");
                System.out.println(
                        "1. Kode Akses\n2. PIN\n3. No. Telp\n4. Tipe Rekening\n5. Status Pernikahan\n6. Tipe Tempat Tinggal\n7. Pendidikan Terakhir\n8. Pekerjaan\n9. Kembali ke menu awal");
                garisBatas();
                System.out.print("Pilih data yang akan diubah/edit: ");
                menu = Integer.parseInt(rd.readLine());
                garisBatas();
                switch (menu) {
                    case 1: // ubah kode akses
                        System.out.print("Masukkan kode akses yang baru: ");
                        accessCode = rd.readLine();
                        garisBatas();
                        clientAccessCode.set(temp1, accessCode);
                        break;

                    case 2: // ubah pin
                        System.out.print("Masukkan PIN yang baru: ");
                        pin = Integer.parseInt(rd.readLine());
                        garisBatas();
                        clientPIN.set(temp1, pin);
                        break;

                    case 3: // ubah no. telp
                        System.out.print("Masukkan no. Telp yang baru: ");
                        phone = rd.readLine();
                        garisBatas();
                        clientPhone.set(temp1, phone);
                        break;

                    case 4: // ubah tipe rekening
                        engineSet(accType = 0, clientAccType, data_AccType, "tipe rekening", "Tipe Rekening");
                        break;

                    case 5: // ubah status pernikahan
                        engineSet(mrgState = 0, clientMrgState, data_MrgState, "status perkawinan",
                                "Status Perkawinan");
                        break;

                    case 6: // ubah tipe tempat tinggal
                        engineSet(dwellType = 0, clientDwellType, data_DwellType, "tipe tempat tinggal",
                                "Tipe Tempat Tinggal");
                        break;

                    case 7: // ubah pendidikan terakhir
                        engineSet(lastSchool = 0, clientLastSchool, data_LastSchool, "pendidikan terakhir",
                                "Pendidikan Terakhir");
                        break;

                    case 8: // ubah pekerjaan
                        engineSet(job = 0, clientJob, data_Job, "pekerjaan", "Pekerjaan");
                        break;

                    case 9: // kembali ke menu
                        menuAdmin();
                        break;
                }
                garisBatas();
                System.out.println("Perubahan Berhasil");
                printDataNasabah(temp1);
                garisBatas();
                break;

            case 2: // hapus data nasabah
                System.out.print("Apakah Anda yakin ingin hapus data nasabah? (Ya : 1/Tidak : 0): ");
                menu = Integer.parseInt(rd.readLine());
                garisBatas();
                if (menu == 1) {
                    engineDelete(temp1);
                    System.out.println("Data Terhapus");
                } else {
                    menuAdmin();
                }
                break;

            case 3: // kembali ke menu
                menuAdmin();
                break;
        }
    }

    static void prosesCariDataNasabah(int index) throws IOException {
        switch (index) {
            case 1: // cari no rek
                System.out.print("Masukkan No. Rekening yang dicari: ");
                accNumStr = rd.readLine();
                if (clientAccountNum.contains(accNumStr) == true) {
                    temp1 = clientAccountNum.indexOf(accNumStr);
                    printDataNasabah(temp1);
                    if (edit == true) {
                        advanceSearch();
                    }
                } else {
                    if (accNumStr.length() != 10) {
                        System.out.println("Maaf, no. rekening Anda tidak/melebihi 10 digit");
                    } else {
                        System.out.println("Maaf, no. rekening yang Anda masukkan tidak terdaftar");
                        System.out.print("Apakah Anda ingin mencari lagi? (Ya : 1/Tidak : 2): ");
                        menu = Integer.parseInt(rd.readLine());
                        if (menu == 1) {
                            prosesCariDataNasabah(index);
                        } else {
                            menuAdmin();
                        }
                    }
                }
                break;

            case 2: // cari NIK
                System.out.print("Masukkan NIK yang dicari: ");
                nik = rd.readLine();
                if (clientNIK.contains(nik) == true) {
                    temp1 = clientNIK.indexOf(nik);
                    printDataNasabah(temp1);
                    if (edit == true) {
                        advanceSearch();
                    }
                } else {
                    if (nik.length() != 16) {
                        System.out.println("Maaf, NIK Anda tidak/melebihi 16 digit");
                    } else {
                        System.out.println("Maaf, NIK yang Anda masukkan tidak terdaftar");
                        System.out.print("Apakah Anda ingin mencari lagi? (Ya : 1/Tidak : 2): ");
                        menu = Integer.parseInt(rd.readLine());
                        if (menu == 1) {
                            prosesCariDataNasabah(index);
                        } else {
                            menuAdmin();
                        }
                    }
                }
                break;

            case 3: // cari nama
                System.out.print("Masukkan Nama yang dicari: ");
                name = rd.readLine();
                if (clientName.contains(name) == true) {
                    for (int i = 0; i < clientName.size(); i++) {
                        temp = (String) clientName.get(i);
                        if (temp.equals(name)) {
                            printDataNasabah(i);
                        }
                    }
                } else {
                    if (name.length() < 3) {
                        System.out.println("Maaf, nama yang Anda masukkan kurang dari 3 digit");
                    } else {
                        System.out.println("Maaf, nama yang Anda masukkan tidak terdaftar");
                        System.out.print("Apakah Anda ingin mencari lagi? (Ya : 1/Tidak : 2): ");
                        menu = Integer.parseInt(rd.readLine());
                        if (menu == 1) {
                            prosesCariDataNasabah(index);
                        } else {
                            menuAdmin();
                        }
                    }
                }
                break;

            case 4: // cari no telp
                System.out.print("Masukkan no. Telp yang dicari: ");
                phone = rd.readLine();
                if (clientPhone.contains(phone) == true) {
                    temp1 = clientPhone.indexOf(phone);
                    printDataNasabah(temp1);
                    if (edit == true) {
                        advanceSearch();
                    }
                } else {
                    if (phone.length() < 10 || phone.length() > 13) {
                        System.out.println(
                                "Maaf, no. Telp yang Anda cari tidak dapat kurang dari 10 dan lebih dari 13 digit");
                    } else {
                        System.out.println("Maaf, no. Telp yang Anda masukkan tidak terdaftar");
                        System.out.print("Apakah Anda ingin mencari lagi? (Ya : 1/Tidak : 2): ");
                        menu = Integer.parseInt(rd.readLine());
                        if (menu == 1) {
                            prosesCariDataNasabah(index);
                        } else {
                            menuAdmin();
                        }
                    }
                }
                break;

            case 5: // cari tipe rekening
                engineSearch(accType = 0, clientAccType, data_AccType, "tipe rekening", "Tipe Rekening", index);
                break;

            case 6: // cari status pernikahan
                engineSearch(mrgState = 0, clientMrgState, data_MrgState, "status perkawinan", "Status Perkawinan",
                        index);
                break;

            case 7: // cari tipe tempat tinggal
                engineSearch(dwellType = 0, clientDwellType, data_DwellType, "tipe tempat tinggal",
                        "Tipe Tempat Tinggal", index);
                break;

            case 8: // cari pendidikan terakhir
                engineSearch(lastSchool = 0, clientLastSchool, data_LastSchool, "pendidikan terakhir",
                        "Pendidikan Terakhir", index);
                break;

            case 9: // cari pekerjaan
                engineSearch(job = 0, clientJob, data_Job, "pekerjaan", "Pekerjaan", index);
                break;

            case 10: // kembali
                menuAdmin();
                break;
        }
    }

    static void engineSearch(int var, Vector storage, Vector data, String kata, String judul, int index)
            throws IOException {
        System.out.println(judul);
        for (int i = 0; i < data.size(); i++) {
            System.out.println((i + 1) + ". " + data.elementAt(i));
        }
        garisBatas();
        System.out.print("Masukkan " + kata + " yang dicari: ");
        var = (Integer.parseInt(rd.readLine())) - 1;
        if (storage.contains(var) == true) {
            for (int i = 0; i < storage.size(); i++) {
                temp1 = (Integer) storage.get(i);
                if (temp1.compareTo(var) == 0) {
                    printDataNasabah(i);
                }
            }
        } else {
            if (var >= data.size()) {
                System.out.println("Maaf, " + kata + " yang Anda masukkan tidak tersedia");
                System.out.print("Apakah Anda ingin mencari lagi? (Ya : 1/Tidak : 2): ");
                menu = Integer.parseInt(rd.readLine());
                garisBatas();
                if (menu == 1) {
                    prosesCariDataNasabah(index);
                } else {
                    menuAdmin();
                }
            } else {
                System.out.println("Maaf, data " + kata + " nasabah yang Anda cari kosong");
            }
        }
    }

    static void engineSet(int var, Vector storage, Vector data, String kata, String judul) throws IOException {
        System.out.println(judul);
        for (int i = 0; i < data.size(); i++) {
            System.out.println((i + 1) + ". " + data.elementAt(i));
        }
        garisBatas();
        System.out.print("Masukkan " + kata + " yang baru: ");
        var = (Integer.parseInt(rd.readLine())) - 1;
        garisBatas();
        storage.set(temp1, var);
    }

    static void engineDelete(int index) throws IOException {
        clientNIK.remove(index);
        clientName.remove(index);
        clientPhone.remove(index);
        clientAccType.remove(index);
        clientMrgState.remove(index);
        clientDwellType.remove(index);
        clientLastSchool.remove(index);
        clientJob.remove(index);
        clientAccountNum.remove(index);
        clientAccessCode.remove(index);
        clientPIN.remove(index);
        clientBalance.remove(index);
        clientAvl.remove(index);
    }

    static void menuUser() throws IOException {
        Boolean loop = true;
        do {
            garisBatas();
            System.out.println("====== USER INFO ======");
            System.out.println("\nNo. Rekening\t: " + clientAccountNum.get(clientAccessCode.indexOf(username)));
            System.out.println("Nama Nasabah\t: " + clientName.get(clientAccessCode.indexOf(username)));
            System.out.println("\n====== MENU ======");
            System.out.println(
                    "1. Setor Tunai\n2. Tarik Tunai\n3. Transfer\n4. Cetak Saldo\n5. Cetak Mutasi\n6. Tutup Rekening\n7. Keluar");
            garisBatas();
            System.out.print("Masukkan index menu yang diinginkan (1-7): ");
            menu = Integer.parseInt(rd.readLine());
            garisBatas();
            switch (menu) {
                case 1: // setor tunai
                    System.out.print("Masukkan nominal yang akan disetor: ");
                    nominal = Integer.parseInt(rd.readLine());
                    tempBal = (int) (clientBalance.get(clientAccessCode.indexOf(username)));
                    tempBal += nominal;
                    clientBalance.set(clientAccessCode.indexOf(username), tempBal);
                    System.out.println("Uang Berhasil Disetor !");
                    System.out.println("\nSaldo Anda Rp " + tempBal);
                    indHistory.add(username);
                    history.add("Setor Tunai\t\t\tRp " + nominal);
                    break;

                case 2: // tarik tunai
                    System.out.print("Masukkan nominal yang akan ditarik: ");
                    nominal = Integer.parseInt(rd.readLine());
                    tempBal = (int) (clientBalance.get(clientAccessCode.indexOf(username)));
                    if (tempBal - nominal >= MIN_SALDO) {
                        tempBal -= nominal;
                        clientBalance.set(clientAccessCode.indexOf(username), tempBal);
                        System.out.println("Uang Berhasil Ditarik !");
                        System.out.println("\nSaldo Anda tersisa Rp " + tempBal);
                        indHistory.add(username);
                        history.add("Tarik Tunai\t\t\tRp " + nominal);
                    } else {
                        System.out.println("Tarik Tunai Gagal, Saldo Tidak Cukup");
                    }
                    break;

                case 3: // transfer
                    System.out.print("Masukkan no. rekening yang dituju: ");
                    destAccount = rd.readLine();
                    if (clientAccountNum.contains(destAccount) == false) {
                        System.out.println("Maaf, no. rekening yang Anda tuju tidak terdaftar");
                    } else {
                        if ((Boolean) (clientAvl.get(clientAccountNum.indexOf(destAccount))) == false) {
                            System.out.println("Maaf, no. rekening yang Anda tuju tidak terdaftar");
                        } else {
                            System.out.print("Masukkan nominal yang akan ditransfer (biaya : Rp 5000): ");
                            nominal = (Integer.parseInt(rd.readLine())) + 5000;
                            tempBal = (int) (clientBalance.get(clientAccessCode.indexOf(username)));
                            if (tempBal - nominal >= MIN_SALDO) {
                                tempBal -= nominal;
                                clientBalance.set(clientAccessCode.indexOf(username), tempBal);
                                clientBalance.set((int) clientAccountNum.indexOf(destAccount),
                                        (int) (clientBalance.get(clientAccountNum.indexOf(destAccount)))
                                                + (nominal - 5000));
                                System.out.println("Uang Berhasil Ditransfer !");
                                System.out.println("\nSaldo Anda tersisa Rp " + tempBal);
                                // catat mutasi
                                indHistory.add(username);
                                history.add("Kirim Uang (" + destAccount + ")\t\tRp " + (nominal - 5000));
                                indHistory.add(clientAccessCode.get((int) (clientAccountNum.indexOf(destAccount))));
                                history.add("Terima Uang (" + clientAccountNum.get(clientAccessCode.indexOf(username))
                                        + ")\tRp " + (nominal - 5000));
                            } else {
                                System.out.println("Transfer Gagal, Saldo Tidak Cukup !!");
                            }
                        }
                    }
                    break;

                case 4: // cetak saldo
                    tempBal = (int) (clientBalance.get(clientAccessCode.indexOf(username)));
                    System.out.print("Saldo Anda Sebesar: Rp " + tempBal + "\n");
                    break;

                case 5: // cetak mutasi
                    System.out.println("====== CETAK MUTASI ======");
                    if (indHistory.contains(username) == false) {
                        System.out.println("KOSONG !!");
                    } else {
                        System.out.println("KETERANGAN\t\t\tNOMINAL");
                        for (int i = 0; i < indHistory.size(); i++) {
                            temp = (String) indHistory.get(i);
                            if (username.equals(temp)) {
                                System.out.println(history.get(i));
                            }
                        }
                        System.out
                                .println("\nSISA SALDO\t\t\tRp "
                                        + clientBalance.get(clientAccessCode.indexOf(username)));
                    }
                    break;

                case 6: // tutup rekening
                    System.out.print("1. Ya\n2. Tidak\nApakah Anda yakin untuk tutup rekening? : ");
                    menu = Integer.parseInt(rd.readLine());
                    if (menu == 1) {
                        clientAvl.set(clientAccessCode.indexOf(username), false);
                        System.out.println("Anda telah mengambil uang Anda sebesar\n\tRp "
                                + ((int) (clientBalance.get(clientAccessCode.indexOf(username))) - MIN_SALDO));
                        clientBalance.set(clientAccessCode.indexOf(username), MIN_SALDO);
                        System.out.println("Akun Anda telah ditutup\nKembali ke menu login...");
                        login();
                    }
                    break;

                case 7: // keluar
                    System.out.println("Log out...");
                    login();
                    break;

                default:
                    System.out.println("Maaf, index yang Anda tuju tidak ada");
                    break;
            }
        } while (loop == true);
    }

    static void inputData() {
        String[] arr_AccType = { "Bronze", "Silver", "Gold" };
        String[] arr_MrgState = { "Kawin", "Belum Kawin" };
        String[] arr_DwellType = { "Rumah Sendiri", "Rumah Orang Tua", "Kontrakan", "Tempat Tinggal Lainnya" };
        String[] arr_LastSchool = { "SD", "SMP", "SMA/SMK", "S1", "S2", "S3" };
        String[] arr_Job = { "PNS", "Pegawai Swasta", "Pekerjaan Lainnya" };

        data_AccType = new Vector<String>(Arrays.asList(arr_AccType));
        data_MrgState = new Vector<String>(Arrays.asList(arr_MrgState));
        data_DwellType = new Vector<String>(Arrays.asList(arr_DwellType));
        data_LastSchool = new Vector<String>(Arrays.asList(arr_LastSchool));
        data_Job = new Vector<String>(Arrays.asList(arr_Job));

        String[] dataNIK = { "0000000000000000", "0000000000000001" };
        String[] dataName = { "Andika", "Regi" };
        String[] dataPhone = { "082373497261", "081846290283" };
        Integer[] dataAccType = { 1, 2 };
        Integer[] dataMrgState = { 0, 1 };
        Integer[] dataDwellType = { 0, 0 };
        Integer[] dataLastSchool = { 4, 3 };
        Integer[] dataJob = { 0, 2 };
        String[] dataAccountNum = { "0000000001", "0000000002" };
        String[] dataAccessCode = { "wow123", "nol123" };
        String[] dataPIN = { "123456", "098765" };
        Integer[] dataBalance = { 1500000, 4100000 };
        Boolean[] dataAvl = { true, true };

        clientNIK = new Vector<String>(Arrays.asList(dataNIK));
        clientName = new Vector<String>(Arrays.asList(dataName));
        clientPhone = new Vector<String>(Arrays.asList(dataPhone));
        clientAccType = new Vector<Integer>(Arrays.asList(dataAccType));
        clientMrgState = new Vector<Integer>(Arrays.asList(dataMrgState));
        clientDwellType = new Vector<Integer>(Arrays.asList(dataDwellType));
        clientLastSchool = new Vector<Integer>(Arrays.asList(dataLastSchool));
        clientJob = new Vector<Integer>(Arrays.asList(dataJob));
        clientAccountNum = new Vector<String>(Arrays.asList(dataAccountNum));
        clientAccessCode = new Vector<String>(Arrays.asList(dataAccessCode));
        clientPIN = new Vector<String>(Arrays.asList(dataPIN));
        clientBalance = new Vector<Integer>(Arrays.asList(dataBalance));
        clientAvl = new Vector<Boolean>(Arrays.asList(dataAvl));

        String[] username_admin = { "admin" };
        String[] password_admin = { "admin" };

        adminUsername = new Vector<String>(Arrays.asList(username_admin));
        adminPassword = new Vector<String>(Arrays.asList(password_admin));
    }

    public static void main(String[] args) throws IOException {
        inputData();
        login();
    }
}