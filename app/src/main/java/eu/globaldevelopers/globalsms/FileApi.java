package eu.globaldevelopers.globalsms;

import eu.globaldevelopers.globalsms.Class.DataUserCustomization;
import eu.globaldevelopers.globalsms.Class.SampleResponse;
import eu.globaldevelopers.globalsms.Class.globalwallet.CardQueryResponse;
import eu.globaldevelopers.globalsms.Class.globalwallet.CardResponse;
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
    Call<CardQueryResponse> getQrCardQuery(@Query("terminal") String terminal, @Query("card_number") String card_number);


    @POST(BuildConfig.EP_GLOBALWALLET_TRANSACTION_VALIDATE)
    Call<SampleResponse> validateTransaction(@Query("transaction_id") int transaction_id);


    @GET(BuildConfig.EP_GLOBALWALLET_CARD)
    Call<CardResponse> getQrCard(@Query("card_number") String card_number);
}
