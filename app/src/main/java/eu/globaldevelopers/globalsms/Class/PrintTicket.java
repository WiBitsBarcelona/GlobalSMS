package eu.globaldevelopers.globalsms.Class;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.math.BigDecimal;
import eu.globaldevelopers.globalsms.ThreadPoolManager;
import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class PrintTicket {
    private Bitmap HeaderImage;

    private IWoyouService WoyouService;

    private ICallback Callback;

    String Header, Terminal, Fecha, Hora;

    public PrintTicket(IWoyouService woyouService, ICallback callback, String header, String terminal, String fecha, String hora, Bitmap headerImage) {

        Callback = callback;
        WoyouService = woyouService;
        Header = header;
        Terminal = terminal;
        Fecha = fecha;
        Hora = hora;
        HeaderImage = headerImage;
    }

    public void printFinishTicket(final Double rDiesel, final Double rAdBlue, final Double rRedDiesel,
                                  final Double rGas, final Double AuthMoney, final String codigo,
                                  final String Dieselprice, final String Adblueprice, final String RedDieselprice,
                                  final String Gasprice, final Boolean showPrices) {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            String msg, totalTxt;

            @Override
            public void run() {
                try {
                    msg = "TRANSACTION SUCCESSFULLY\n";
                    msg += "COMPLETED";
                    msg += "\n";
                    WoyouService.lineWrap(2, Callback);
                    WoyouService.setAlignment(1, Callback);
                    WoyouService.printBitmap(HeaderImage, Callback);
                    WoyouService.setFontSize(24, Callback);
                    WoyouService.printTextWithFont("\n" + Header + "\n", "", 28, Callback);
                    String pterminal = "Terminal: " + Terminal + "\n\n";
                    WoyouService.printTextWithFont(pterminal, "", 24, Callback);
                    WoyouService.printTextWithFont(Fecha + "   " + Hora + "\n", "", 24, Callback);
                    WoyouService.lineWrap(2, Callback);
                    WoyouService.setAlignment(0, Callback);
                    WoyouService.printTextWithFont("TRX Code: " + codigo + "\n", "", 30, Callback);
                    //WoyouService.printTextWithFont( "Operation Code: " + operation + "\n", "", 30, Callback);
                    WoyouService.printTextWithFont("\n", "", 28, Callback);
                    WoyouService.setFontSize(28, Callback);
                    String[] text = new String[3];
                    int[] width = new int[]{10, 8, 8};
                    int[] align = new int[]{0, 2, 2}; //

                    text[0] = "Product";
                    text[1] = "Liters";
                    text[2] = "Total";
                    WoyouService.printColumnsText(text, width, new int[]{0, 2, 2}, Callback);

                    if (rDiesel > 0) {
                        double total = Float.valueOf(Dieselprice) * rDiesel;
                        BigDecimal a = new BigDecimal(total);
                        final BigDecimal total2 = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                        totalTxt = total2.toString();
                        text[0] = "DIESEL A";
                        text[1] = rDiesel.toString();
                        text[2] = total2.toString();
                        WoyouService.printColumnsText(text, width, align, Callback);
                    }

                    if (rAdBlue > 0) {
                        double total = Float.valueOf(Adblueprice) * rAdBlue;
                        BigDecimal a = new BigDecimal(total);
                        final BigDecimal total2 = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                        totalTxt = total2.toString();
                        text[0] = "AD BLUE";
                        text[1] = rAdBlue.toString();
                        text[2] = total2.toString();
                        WoyouService.printColumnsText(text, width, align, Callback);
                    }

                    if (rRedDiesel > 0) {
                        double total = Float.valueOf(RedDieselprice) * rRedDiesel;
                        BigDecimal a = new BigDecimal(total);
                        final BigDecimal total2 = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                        totalTxt = total2.toString();
                        text[0] = "D. ROJO";
                        text[1] = rRedDiesel.toString();
                        text[2] = total2.toString();
                        WoyouService.printColumnsText(text, width, align, Callback);
                    }

                    if (rGas > 0) {
                        double total = Float.valueOf(Gasprice) * rGas;
                        BigDecimal a = new BigDecimal(total);
                        final BigDecimal total2 = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                        totalTxt = total2.toString();
                        text[0] = "GAS";
                        text[1] = rGas.toString();
                        text[2] = total2.toString();
                        WoyouService.printColumnsText(text, width, align, Callback);
                    }

                    if (AuthMoney > 0) {
                        text[0] = "ENTREGA";
                        text[1] = " ";
                        text[2] = AuthMoney.toString();
                        WoyouService.printColumnsText(text, width, align, Callback);
                    }

                    WoyouService.lineWrap(2, Callback);
                    //WoyouService.printBitmap(bitmap, Callback);
                    WoyouService.setAlignment(1, Callback);
                    WoyouService.printTextWithFont(msg, "", 32, Callback);
                    WoyouService.lineWrap(4, Callback);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
    }
}
