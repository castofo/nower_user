package castofo.com.co.nower.services;

import java.util.List;

import castofo.com.co.nower.models.Branch;
import castofo.com.co.nower.models.Promo;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Alejandro on 10/01/2017.
 */
public interface MapService {

  @GET("/v1/branches")
  Single<List<Branch>> getNearbyBranches(@Query("latitude") double latitude,
                                         @Query("longitude") double longitude,
                                         @Query("expand") String expand);

  @GET("/v1/branches/{id}")
  Single<Branch> getBranch(@Path("id") String branchId,
                           @Query("expand") String expand);

  @GET("/v1/promos")
  Single<List<Promo>> getBranchPromos(@Query("branch_id") String branchId);
}
