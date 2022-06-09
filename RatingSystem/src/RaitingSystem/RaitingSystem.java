package RaitingSystem;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RaitingSystem {
    public static BufferedReader br;
    int index;
    String raitingListFilePath;
    String[] columns;
    String[] menu;
    File raitingListFile;
    List<List<String>> raitingList;

    public RaitingSystem() throws IOException {
        br = new BufferedReader(new InputStreamReader(System.in));
        this.raitingListFilePath = "/tmp/RaitingList.txt";
        this.columns = new String[]{"{", "No", "businessName", "mainMenu", "rating", "price", "createAt", "updateAt", "},"};
        this.menu = new String[]{"조회", "등록", "삭제", "수정", "저장", "종료"};
        this.raitingListFile = getFile(raitingListFilePath);
        this.raitingList = getRaitingList(raitingListFile, columns);
        index = this.raitingList.size() + 1;
    }

    void run() throws Exception {
        while (true) {
            boolean isExit = false;
            String rawSelector = showMenu(menu);

            if (rawSelector.length() != 1) {
                System.out.println("메뉴 번호를 입력하세요!");
            } else {
                int selector = getSelector(rawSelector);

                switch (selector) {
                    case 1 -> showRaitingList(raitingList, columns);
                    case 2 -> raitingList.add(appendRaitingList(columns));
                    case 3 -> raitingList.remove(removeRaitingList(raitingList, columns));
                    case 4 -> System.out.println("수정");
                    case 5 -> System.out.println("저장");
                    case 6 -> {
                        System.out.println("종료");
                        isExit = true;
                    }
                }
            }

            if (isExit) break;
        }
    }

    static File getFile(String filePath) throws IOException {
        File file = new File(filePath);

        if (file.createNewFile()) {
            try {
                System.out.print("파일 생성중... ");
                System.out.println("소요 시간: " + createFileFormat(file) + "(ns)");
                System.out.println("파일 생성 성공!");
            } catch (Exception e) {
                System.out.println("파일 생성 실패!");
                System.out.println("Reason: " + e);
            }
        } else {
            System.out.println("환영합니다!");
        }

        return file;
    }

    static long createFileFormat(File file) throws IOException {
        PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));

        long startTime = System.nanoTime();

        printWriter.println("{");
        printWriter.println("\t" + "status: 200,");
        printWriter.println("\t" + "data: [");
        printWriter.println("\t\t");
        printWriter.println("\t" + "]");
        printWriter.println("}");
        printWriter.close();

        long finishTime = System.nanoTime();

        return finishTime - startTime;
    }

    static List<List<String>> getRaitingList(File raitingListFile, String[] columns) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(raitingListFile));
        List<List<String>> raitingList = new ArrayList<>();

        String row;

        List<String> singleRaiting = new ArrayList<>();

        while ((row = br.readLine()) != null) {
            if (singleRaiting.size() == 7) {
                raitingList.add(singleRaiting);
                singleRaiting = new ArrayList<>();
            }

            for (int i = 1; i < columns.length - 1; i++) {
                if (row.contains(columns[i])) {
                    singleRaiting.add(row.split(":")[1].trim());
                }
            }
        }

        br.close();

        return raitingList;
    }

    static int getSelector(String rawSelector) {
        try {
            return Integer.parseInt(rawSelector);
        } catch (NumberFormatException e) {
            System.out.println("올바른 메뉴를 입력하세요!");
            System.out.println("Reason: " + e);
            return -1;
        }
    }

    static void showRaitingList(List<List<String>> raitingList, String[] columns) {
        IntStream
                .range(0, raitingList.size())
                .forEach(row -> showRaiting(raitingList, columns, row));
    }

    static void showRaiting(List<List<String>> raitingList, String[] columns, int row) {
        System.out.println(columns[0]);

        IntStream
                .range(0, raitingList.get(row).size())
                .forEach(col -> System.out.println("\t" + columns[col + 1] + ": " + raitingList.get(row).get(col)));

        System.out.println(columns[columns.length - 1]);
    }

    List<String> appendRaitingList(String[] columns) {
        List<String> raiting = new ArrayList<>();

        IntStream.range(1, columns.length - 3).forEach(index -> {
            try {
                if (index == 1) {
                    raiting.add(String.valueOf(this.index++));
                } else {
                    appendRaiting(raiting, columns, index);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // createAt, updateAt
        raiting.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeFormat())));
        raiting.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeFormat())));

        return raiting;
    }

    static void appendRaiting(List<String> raiting, String[] columns, int index) throws IOException {
        System.out.print(columns[index] + ": ");
        raiting.add(br.readLine());
    }

    static int removeRaitingList(List<List<String>> raitingList, String[] columns) throws Exception {
        showRaitingList(raitingList, columns);

        System.out.print("Remove No: ");
        int deleteIndex = Integer.parseInt(br.readLine());

        System.out.println(deleteIndex);

        return deleteIndex < raitingList.size() ? deleteIndex - 1 : -1;
    }

    static String timeFormat() {
        return "yyyy년 MM월 dd일 HH시 mm분 ss초";
    }

    static String showMenu(String[] menu) throws IOException {
        IntStream
                .range(0, menu.length)
                .forEach(i -> System.out.println((i + 1) + ". " + menu[i]));

        return br.readLine();
    }
}
