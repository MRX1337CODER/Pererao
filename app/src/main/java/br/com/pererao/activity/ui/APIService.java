package br.com.pererao.activity.ui;

import br.com.pererao.notifications.MyResponse;
import br.com.pererao.notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAsUm7uCk:APA91bE4kUjySbpme4sx-5bmBkl2eQMTNMPAj8g3tLmV19k4sNuD76dyb9jyse5_MNQq5QHg4nvGkup5SBkk881ri3JH-Kw2Cg6UF2IRMeOYGcq0rwRi9Sx-WUfObbKJFSKwS3zkMGbd"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
