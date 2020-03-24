package eu.globaldevelopers.globalsms;

import java.util.ArrayList;

import eu.globaldevelopers.globalsms.Class.DataUserCustomization;
import eu.globaldevelopers.globalsms.Class.SampleResponse;
import eu.globaldevelopers.globalsms.Class.globalwallet.CardQueryResponse;
import eu.globaldevelopers.globalsms.Class.globalwallet.CardResponse;
import eu.globaldevelopers.globalsms.Class.globalwallet.SaleTransactionResponse;
import eu.globaldevelopers.globalsms.Class.globalwallet.UnlockRequestResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface FileApi {


    @Multipart
    @POST("upload/signature")
    Call<Respond> uploadImage(@Part MultipartBody.Part file);

    @GET(BuildConfig.EP_GLOBALPAY_USER_CUSTOMIZATIONS)
    Call<DataUserCustomization> getUserCustomizations(@Query("customer_code") String customer_code, @Query("terminal") String terminal);

    @POST(BuildConfig.EP_GLOBALWALLET_CARD_QUERY)
    Call<CardQueryResponse> getQrCardQuery(@Query("card_number") String card_number, @Query("terminal") String terminal);


    @POST(BuildConfig.EP_GLOBALWALLET_TRANSACTION_VALIDATE)
    Call<SampleResponse> validateTransaction(@Query("transaction_id") int transaction_id);


    @GET(BuildConfig.EP_GLOBALWALLET_CARD)
    Call<CardResponse> getQrCard(@Query("card_number") String card_number);

    @POST(BuildConfig.EP_GLOBALWALLET_CARD_UNLOCK_REQUEST)
    Call<SampleResponse> sendQrCardUnlockRequest(@Query("card_number") String card_number, @Query("terminal") String terminal);

    @GET(BuildConfig.EP_GLOBALWALLET_CARD_UNLOCK_REQUEST)
    Call<UnlockRequestResponse> getQrCardUnlockRequest(@Query("card_number") String card_number);


    @POST(BuildConfig.EP_GLOBALWALLET_TRANSACTION_SALE)
    Call<SaleTransactionResponse> saveTransactionSale(@Query("card_number") String card_number, @Query("terminal") String terminal, @Query("pump_prices[]") ArrayList<Double> pump_prices, @Query("quantities[]") ArrayList<Double> quantities, @Query("products[]") ArrayList<Integer> products);

}
