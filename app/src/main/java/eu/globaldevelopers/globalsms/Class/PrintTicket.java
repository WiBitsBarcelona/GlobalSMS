package eu.globaldevelopers.globalsms.Class;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.globaldevelopers.globalsms.Class.globalwallet.QrTransaction;
import eu.globaldevelopers.globalsms.Enums.ProductGWIntEnum;
import eu.globaldevelopers.globalsms.R;
import eu.globaldevelopers.globalsms.ThreadPoolManager;
import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class PrintTicket {
    public static final String MyPREFERENCES = "MyPrefs";

    private Bitmap HeaderImage;

    private IWoyouService WoyouService;

    private ICallback Callback;

    String Header, Terminal, Fecha, Hora, PackageName;

    Resources ContextResources;

    Context Context;

    SharedPreferences Sharedpreferences;


    public PrintTicket(IWoyouService woyouService, ICallback callback, String header, String terminal, String fecha, String hora, Bitmap headerImage, Resources resources, String packageName) {

        Callback = callback;
        WoyouService = woyouService;
        Header = header;
        Terminal = terminal;
        Fecha = fecha;
        Hora = hora;
        HeaderImage = headerImage;
        ContextResources = resources;
        PackageName = packageName;
    }

    public PrintTicket(IWoyouService woyouService, ICallback callback, SharedPreferences sharedPreferences, Resources resources, String packageName) {

        Callback = callback;
        WoyouService = woyouService;
        Sharedpreferences = sharedPreferences;
        ContextResources = resources;
        PackageName = packageName;

    }

    public void printFinishTicket(final Double rDiesel, final Double rAdBlue, final Double rRedDiesel,
                                  final Double rGas, final Double AuthMoney, final String codigo,
                                  final Double Dieselprice, final Double Adblueprice, final Double RedDieselprice,
                                  final Double Gasprice, final Boolean showPrices, final String plate, final String trailerPlate) {
        try {
            for (int g = 0; g < 2; g++) {
                ThreadPoolManager.getInstance().executeTask(new Runnable() {
                    String msg, totalTxt;

                    @Override
                    public void run() {
                        try {
                            msg = "TRANSACCION FINALIZADA\n";
                            msg += "COMPLETADA";
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
                            WoyouService.printTextWithFont("Codigo TRX: " + codigo + "\n", "", 30, Callback);
                            //WoyouService.printTextWithFont( "Operation Code: " + operation + "\n", "", 30, Callback);
                            WoyouService.printTextWithFont("Matrícula: " + plate + "\n", "", 30, Callback);
                            WoyouService.printTextWithFont("Mat remolque: " + trailerPlate + "\n", "", 30, Callback);
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

    public void printTransactionValidated(final QrTransaction transaction) {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            String msg, totalTxt;

            @Override
            public void run() {
                try {
                    msg = ContextResources.getString(R.string.transaction_pending_ticket);
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
                    WoyouService.printTextWithFont("TRX Code: " + transaction.id + "\n", "", 30, Callback);
                    //WoyouService.printTextWithFont( "Operation Code: " + operation + "\n", "", 30, Callback);
                    WoyouService.printTextWithFont("\n", "", 28, Callback);

                    //AUTH PRODUCTS
                    WoyouService.setAlignment(1, Callback);
                    WoyouService.printTextWithFont(ContextResources.getString(R.string.authorized_products), "", 28, Callback);
                    WoyouService.printTextWithFont("\n\n", "", 28, Callback);

                    for (Product product : transaction.card.type.products) {
                        int productId = ContextResources.getIdentifier(product.lang_code, "string", PackageName);
                        WoyouService.printTextWithFont(ContextResources.getString(productId), "", 26, Callback);
                        WoyouService.printTextWithFont("\n", "", 26, Callback);
                    }

                    WoyouService.setAlignment(1, Callback);
                    WoyouService.printTextWithFont("\n", "", 28, Callback);
                    WoyouService.printTextWithFont(ContextResources.getString(R.string.max_quantity), "", 28, Callback);
                    WoyouService.printTextWithFont("\n\n", "", 28, Callback);

                    WoyouService.printTextWithFont(transaction.max_quantity.toString(), "", 28, Callback);

                    //END OF TICKET (FOOTER)
                    WoyouService.lineWrap(2, Callback);
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

    public void printTransactionCompleted(final QrTransaction[] transactions) {
        final String header = Sharedpreferences.getString("cabeceraKey", null) + "\n";
        final String terminal = Sharedpreferences.getString("terminalKey", null);
        final String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        final Bitmap mBitmap = BitmapFactory.decodeResource(ContextResources, R.drawable.globalsms);

        try {
            for (int g = 0; g < 2; g++) {
                ThreadPoolManager.getInstance().executeTask(new Runnable() {
                    String msg, totalTxt;

                    @Override
                    public void run() {
                        try {
                            msg = ContextResources.getString(R.string.transaction_completed_ticket);
                            msg += "\n";
                            WoyouService.lineWrap(2, Callback);
                            WoyouService.setAlignment(1, Callback);
                            WoyouService.printBitmap(mBitmap, Callback);
                            WoyouService.setFontSize(24, Callback);
                            WoyouService.printTextWithFont("\n" + header + "\n", "", 28, Callback);
                            String pterminal = "Terminal: " + terminal + "\n\n";
                            WoyouService.printTextWithFont(pterminal, "", 24, Callback);
                            WoyouService.printTextWithFont(date + "   " + time + "\n", "", 24, Callback);
                            WoyouService.lineWrap(2, Callback);
                            WoyouService.setAlignment(0, Callback);

                            //Transaction IDs
                            WoyouService.printTextWithFont("TRX Codes: ", "", 30, Callback);

                            for (int i = 0; i < transactions.length; i++) {
                                String text = "";
                                if (i == 0) {
                                    text += transactions[i].id;
                                } else {
                                    text += '-';
                                    text += transactions[i].id;
                                }

                                WoyouService.printTextWithFont(text, "", 28, Callback);
                            }

                            WoyouService.printTextWithFont("\n\n", "", 28, Callback);

                            int fontSize = 28;
                            int numColumns = 3;
                            int[] width, align;
                            width = new int[]{10, 8, 8};
                            align = new int[]{0, 2, 2};

                            WoyouService.setFontSize(fontSize, Callback);

                            String[] text = new String[numColumns];
                            int column = 0;
                            text[column] = "Product";
                            column++;
                            text[column] = "Liters";
                            column++;
                            text[column] = "Total";
                            WoyouService.printColumnsText(text, width, align, Callback);

                            for(QrTransaction transaction : transactions){
                                double total;
                                BigDecimal formatTotal;
                                BigDecimal totalFormated;
                                column = 0;

                                text[column] = transaction.product.name;
                                column++;
                                text[column] = transaction.quantity.toString();
                                column++;

                                total = transaction.pump_price * transaction.quantity;
                                formatTotal = new BigDecimal(total);
                                totalFormated = formatTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                text[column] = totalFormated.toString();

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
