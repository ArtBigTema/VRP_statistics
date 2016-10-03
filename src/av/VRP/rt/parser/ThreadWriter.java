package av.VRP.rt.parser;

import av.VRP.rt.Utils.HttpApi;
import av.VRP.rt.listener.FileWriterListener;
import av.VRP.rt.listener.VRPgeneratorListener;

import java.io.*;

/**
 * Created by Artem on 10.04.2016.
 */
public class ThreadWriter extends Thread implements Runnable {//FIXME all
    public int num = 200_000;//count in part
    public InputStream in;//FIXME remove public
    public FileWriterListener listener;

    public ThreadWriter(FileWriterListener listener) {
        setListener(listener);

        //    new File("Files").mkdir();
    }

    public void setListener(FileWriterListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        listener.started();

        String line = "";
        String fline = "";
        int i = 0;
        BufferedReader br = null;

        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream("D:/db/export/e4/test.txt"), "windows-1251"));
            //     br.readLine();
            fline = br.readLine();
            line = br.readLine();
            while (line != null) {
                int count = num;
                i++;
                StringBuilder sb = new StringBuilder("Номер документа|Серия документа|Фамилия|Имя|Отчество|Дата рождения|НП/Район|Улица|Дом|Дом литера|Корпус|Квартира|Квартира литера|");
                // newLine.append("\r\n");
//Системный номер|ENP|Номер документа|Серия документа|Тип документа|Фамилия|Имя|Отчество|Статус|Дата рождения|Номер полиса|Серия полиса|НП/Район|Улица|Дом|Дом литера|Корпус|Квартира|Квартира литера|Дата начала|Дата окончания|Страховая компания|ЛПУ ПМСП прикрепления|ЛПУ ПМСП по месту жительства|стоматологическое ЛПУ ПМСП|LPUBASE|LPUBASE_U|LPUDENT|INSURER|ID фио+дата
                // 2 3  5 6 7 9 10 12 13 14 ... 18
                while (line != null && count > 0) {
                    StringBuilder newLine = new StringBuilder();
                    String[] str = line.split("\\|");

                    if (str[9].split("\\.").length > 2) {
                        int year = Integer.parseInt(str[9].split("\\.")[2]);

                        if (year < 1966) {
                            line = br.readLine();
                            continue;
                        }
                    } else {
                        line = br.readLine();
                        continue;
                    }

                    newLine.append("\r\n");
                    newLine.append(str[2]);
                    newLine.append("|");
                    newLine.append(str[3]);
                    newLine.append("|");
                    newLine.append(str[5]);
                    newLine.append("|");
                    newLine.append(str[6]);
                    newLine.append("|");
                    newLine.append(str[7]);
                    newLine.append("|");
                    newLine.append(str[9]);
                    newLine.append("|");//date bd
                    newLine.append(str[12]);
                    newLine.append("|");
                    newLine.append(str[13]);
                    newLine.append("|");
                    newLine.append(str[14]);
                    newLine.append("|");
                    newLine.append(str[15]);
                    newLine.append("|");
                    newLine.append(str[16]);
                    newLine.append("|");
                    newLine.append(str[17]);
                    newLine.append("|");
                    newLine.append(str[18]);
                    newLine.append("|");

                    count--;
                    sb.append(newLine.toString());
                    line = br.readLine();
                }
                String everything = sb.toString();
                PrintWriter writer = getWriter(i);
                writer.append(everything);
                writer.append(System.lineSeparator());
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {//FIXME
            listener.stoped();
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public PrintWriter getWriter(int i) throws FileNotFoundException, UnsupportedEncodingException {
        return new PrintWriter("D:/db/export/e4/" + i + ".txt", "UTF-8");
    }
}