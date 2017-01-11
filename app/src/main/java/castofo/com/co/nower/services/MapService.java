package castofo.com.co.nower.services;

import java.util.List;

import castofo.com.co.nower.models.Branch;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Alejandro on 10/01/2017.
 */
public interface MapService {

  @GET("/v1/branches")
  Observable<List<Branch>> getNearbyBranches(@Query("latitude") double latitude,
                                             @Query("longitude") double longitude);
}
