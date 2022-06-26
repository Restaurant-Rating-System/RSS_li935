package RatingSystem;

import Domain.RatingDTO;
import Util.ColumnUtil;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RatingSystem {
    public static BufferedReader br;
    long index;
    String ratingListFilePath;
    String[] columns;
    String[] menu;
    File ratingListFile;
    List<RatingDTO> ratingList;

    public RatingSystem() throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        br = new BufferedReader(new InputStreamReader(System.in));
        this.ratingListFilePath = "/tmp/RatingList.txt";
        this.columns = new String[]{"id", "businessName", "mainMenu", "rating", "price", "createAt", "updateAt"};
        this.menu = new String[]{"조회", "등록", "삭제", "수정", "저장", "종료"};
        this.ratingListFile = getFile(ratingListFilePath);
        this.ratingList = getRatingList(ratingListFile, columns);
        index = this.ratingList.size() + 1;
    }

    public void run() throws Exception {
        while (true) {
            boolean isExit = false;
            String rawSelector = showMenu(menu);

            if (rawSelector.length() != 1) {
                System.out.println("메뉴 번호를 입력하세요!");
            } else {
                int selector = getSelector(rawSelector);

                switch (selector) {
                    case 1:
                        showRatingList(ratingList, columns);
                        break;
                    case 2:
                        ratingList.add(appendRatingList(columns));
                        break;
                    case 3:
                        ratingList.remove(removeRatingList(ratingList, columns));
                        index--;
                        break;
                    case 4:
                        System.out.println("수정");
                        break;
                    case 5:
                        saveRatingList(ratingList, columns, this.ratingListFilePath);
                        break;
                    case 6:
                        System.out.println("종료");
                        isExit = true;
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
        long startTime = System.nanoTime();

        PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        printWriter.close();

        long finishTime = System.nanoTime();

        return finishTime - startTime;
    }

    static List<RatingDTO> getRatingList(File ratingListFile, String[] columns) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BufferedReader br = new BufferedReader(new FileReader(ratingListFile));

        List<RatingDTO> ratingList = new ArrayList<>();
        RatingDTO ratingDTO = new RatingDTO();

        String row;

        while ((row = br.readLine()) != null) {
            for (String column : columns) {
                if (row.contains(column)) {
                    if (row.contains("id")) {
                        Method method = RatingDTO.class.getDeclaredMethod("set" + new ColumnUtil(column).getCamelCaseColumn(), Long.class);
                        method.setAccessible(true);
                        method.invoke(ratingDTO, Long.parseLong(row.split(":")[1].trim()));
                    } else {
                        Method method = RatingDTO.class.getDeclaredMethod("set" + new ColumnUtil(column).getCamelCaseColumn(), String.class);
                        method.setAccessible(true);
                        method.invoke(ratingDTO, row.split(":")[1].trim());
                    }

                    break;
                }
            }

            if (ratingDTO.getUpdateAt() != null) {
                ratingList.add(ratingDTO);
                ratingDTO = new RatingDTO();
            }
        }

        br.close();

        return ratingList;
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

    static void showRatingList(List<RatingDTO> ratingList, String[] columns) {
        IntStream
                .range(0, ratingList.size())
                .forEach(row -> {
                    try {
                        showRating(ratingList.get(row), columns);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    static void showRating(RatingDTO ratingDTO, String[] columns) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        IntStream
                .range(0, columns.length)
                .forEach(index -> {
                    try {
                        System.out.println(
                                columns[index]
                                        + ": "
                                        + ratingDTO.getClass().getMethod("get" + new ColumnUtil(columns[index]).getCamelCaseColumn()).invoke(ratingDTO).toString()
                        );
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    RatingDTO appendRatingList(String[] columns) {
        RatingDTO ratingDTO = new RatingDTO();

        IntStream.range(1, columns.length - 2).forEach(index -> {
            try {
                appendRating(ratingDTO, columns, index);
            } catch (IOException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        String timeFormat = "yyyy년 MM월 dd일 HH시 mm분 ss초";

        ratingDTO.setCreateAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeFormat)));
        ratingDTO.setUpdateAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeFormat)));
        ratingDTO.setId(index++);

        return ratingDTO;
    }

    static void appendRating(RatingDTO ratingDTO, String[] columns, int index) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        System.out.print(columns[index] + ": ");

        Method method = RatingDTO.class.getDeclaredMethod("set" + new ColumnUtil(columns[index]).getCamelCaseColumn(), String.class);
        method.setAccessible(true);
        method.invoke(ratingDTO, br.readLine());
    }

    // TODO: 사후 정렬처리 필요
    int removeRatingList(List<RatingDTO> ratingList, String[] columns) throws Exception {
        showRatingList(ratingList, columns);

        System.out.print("\nRemove No: ");
        int deleteIndex = Integer.parseInt(br.readLine());

        index--;
        return deleteIndex - 1;
    }

    static void saveRatingList(List<RatingDTO> ratingList, String[] columns, String ratingListFilePath) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        File file = new File(ratingListFilePath);
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        for (RatingDTO ratingDTO : ratingList) {
            for (String column : columns) {
                String row = column + ": " + ratingDTO.getClass().getMethod("get" + new ColumnUtil(column).getCamelCaseColumn()).invoke(ratingDTO).toString();
                printWriter.println(row);
            }
        }

        printWriter.close();
    }

    static String showMenu(String[] menu) throws IOException {
        IntStream
                .range(0, menu.length)
                .forEach(i -> System.out.println((i + 1) + ". " + menu[i]));

        return br.readLine();
    }
}
