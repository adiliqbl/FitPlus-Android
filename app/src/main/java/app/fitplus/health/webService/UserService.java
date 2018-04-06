package app.fitplus.health.webService;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface UserService {

    @Headers("Accept: application/json")
    @GET("login/{email}/{password}")
    Observable<Response<String>> login(@Path("email") String email, @Path("password") String password);

    @Headers("Accept: application/json")
    @GET("LoginActivity/{email}/{username}/{name}/{password}/{phone}")
    Observable<Response<String>> register(@Path("email") String email, @Path("username") String username,
                                          @Path("name") String name, @Path("password") String password,
                                          @Path("phone") String phone);
}
