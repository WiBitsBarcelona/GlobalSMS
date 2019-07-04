package eu.globaldevelopers.globalsms;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileApi {


    @Multipart
    @POST("upload/signature")
    Call<Respond> uploadImage(@Part MultipartBody.Part file);
}
