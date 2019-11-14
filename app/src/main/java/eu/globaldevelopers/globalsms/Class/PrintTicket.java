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
                                  final Double Dieselprice, final Double Adblueprice, final Double RedDieselprice,
                                  final Double Gasprice, final Boolean showPrices) {
        try {
            for (int g = 0; g < 2; g++) {
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

                            int fontSize = 28;
                            int numColumns = 3;
                            int[] width, align;
                            if (showPrices) {
                                fontSize = 20;
                                numColumns = 4;
                                width = new int[]{10, 8, 8, 8};
                                align = new int[]{0, 2, 2, 2};
                            } else {
                                width = new int[]{10, 8, 8};
                                align = new int[]{0, 2, 2};
                            }

                            WoyouService.setFontSize(fontSize, Callback);

                            String[] text = new String[numColumns];
                            int column = 0;
                            text[column] = "Product";
                            column++;
                            if (showPrices) {
                                text[column] = "Price";
                                column++;
                            }
                            text[column] = "Liters";
                            column++;
                            text[column] = "Total";
                            WoyouService.printColumnsText(text, width, align, Callback);

                            if (rDiesel > 0) {
                                double total = Dieselprice * rDiesel;
                                BigDecimal a = new BigDecimal(total);
                                final BigDecimal total2 = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                totalTxt = total2.toString();
                                column = 0;
                                text[column] = "DIESEL A";
                                column++;
                                if (showPrices) {
                                    text[column] = Dieselprice.toString();
                                    column++;
                                }
                                text[column] = rDiesel.toString();
                                column++;
                                text[column] = total2.toString();
                                WoyouService.printColumnsText(text, width, align, Callback);
                            }

                            if (rAdBlue > 0) {
                                double total = Adblueprice * rAdBlue;
                                BigDecimal a = new BigDecimal(total);
                                final BigDecimal total2 = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                totalTxt = total2.toString();
                                column = 0;
                                text[column] = "AD BLUE";
                                column++;
                                if (showPrices) {
                                    text[column] = Adblueprice.toString();
                                    column++;
                                }
                                text[column] = rAdBlue.toString();
                                column++;
                                text[column] = total2.toString();
                                WoyouService.printColumnsText(text, width, align, Callback);
                            }

                            if (rRedDiesel > 0) {
                                double total = RedDieselprice * rRedDiesel;
                                BigDecimal a = new BigDecimal(total);
                                final BigDecimal total2 = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                totalTxt = total2.toString();
                                column = 0;
                                text[column] = "D. ROJO";
                                column++;
                                if (showPrices) {
                                    text[column] = RedDieselprice.toString();
                                    column++;
                                }
                                text[column] = rRedDiesel.toString();
                                column++;
                                text[column] = total2.toString();
                                WoyouService.printColumnsText(text, width, align, Callback);
                            }

                            if (rGas > 0) {
                                double total = Gasprice * rGas;
                                BigDecimal a = new BigDecimal(total);
                                final BigDecimal total2 = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                totalTxt = total2.toString();
                                column = 0;
                                text[column] = "GAS";
                                column++;
                                if (showPrices) {
                                    text[column] = Gasprice.toString();
                                    column++;
                                }
                                text[column] = rGas.toString();
                                column++;
                                text[column] = total2.toString();
                                WoyouService.printColumnsText(text, width, align, Callback);
                            }

                            if (AuthMoney > 0) {
                                column = 0;
                                text[column] = "ENTREGA";
                                column++;
                                if (showPrices) {
                                    text[column] = " ";
                                    column++;
                                }
                                text[column] = " ";
                                column++;
                                text[column] = AuthMoney.toString();
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
                if (g == 0) {
                    Thread.sleep(5000);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
