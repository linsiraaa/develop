package com.example.one.repository;

import com.example.one.pojo.City;
import com.example.one.pojo.InToHenan;
import com.example.one.pojo.OutOfHenan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrafficRepository extends JpaRepository<City, Integer> {

    // ========== 城市基础查询 ==========

    /**
     * 根据城市名称查询城市
     */
    City findByCityName(String cityName);

    /**
     * 根据省份查询城市列表
     */
    @Query("SELECT c FROM City c WHERE c.province = :province")
    List<City> findCitiesByProvince(@Param("province") String province);

    /**
     * 查询所有入豫记录
     */
    @Query("SELECT i FROM InToHenan i")
    List<InToHenan> findAllInToHenan();

    /**
     * 根据时间ID查询入豫记录
     */
    @Query("SELECT i FROM InToHenan i WHERE i.timeId = :timeId")
    List<InToHenan> findInToHenanByTimeId(@Param("timeId") Integer timeId);

    /**
     * 根据来源城市ID查询入豫记录
     */
    @Query("SELECT i FROM InToHenan i WHERE i.fromCityId = :cityId")
    List<InToHenan> findInToHenanByFromCityId(@Param("cityId") Integer cityId);

    /**
     * 根据目标城市ID查询入豫记录
     */
    @Query("SELECT i FROM InToHenan i WHERE i.toCityId = :cityId")
    List<InToHenan> findInToHenanByToCityId(@Param("cityId") Integer cityId);

    /**
     * 根据城市ID查询相关入豫记录(作为来源或目标)
     */
    @Query("SELECT i FROM InToHenan i WHERE i.fromCityId = :cityId OR i.toCityId = :cityId")
    List<InToHenan> findInToHenanByCityId(@Param("cityId") Integer cityId);

    /**
     * 根据城市ID列表查询相关入豫记录
     */
    @Query("SELECT i FROM InToHenan i WHERE i.fromCityId IN :cityIds OR i.toCityId IN :cityIds")
    List<InToHenan> findInToHenanByCityIds(@Param("cityIds") List<Integer> cityIds);


    /**
     * 查询所有出豫记录
     */
    @Query("SELECT o FROM OutOfHenan o")
    List<OutOfHenan> findAllOutOfHenan();

    /**
     * 根据时间ID查询出豫记录
     */
    @Query("SELECT o FROM OutOfHenan o WHERE o.timeId = :timeId")
    List<OutOfHenan> findOutOfHenanByTimeId(@Param("timeId") Integer timeId);

    /**
     * 根据出发城市ID查询出豫记录
     */
    @Query("SELECT o FROM OutOfHenan o WHERE o.fromCityId = :cityId")
    List<OutOfHenan> findOutOfHenanByFromCityId(@Param("cityId") Integer cityId);

    /**
     * 根据目的城市ID查询出豫记录
     */
    @Query("SELECT o FROM OutOfHenan o WHERE o.toCityId = :cityId")
    List<OutOfHenan> findOutOfHenanByToCityId(@Param("cityId") Integer cityId);

    /**
     * 根据城市ID查询相关出豫记录(作为出发地或目的地)
     */
    @Query("SELECT o FROM OutOfHenan o WHERE o.fromCityId = :cityId OR o.toCityId = :cityId")
    List<OutOfHenan> findOutOfHenanByCityId(@Param("cityId") Integer cityId);

    /**
     * 根据城市ID列表查询相关出豫记录
     */
    @Query("SELECT o FROM OutOfHenan o WHERE o.fromCityId IN :cityIds OR o.toCityId IN :cityIds")
    List<OutOfHenan> findOutOfHenanByCityIds(@Param("cityIds") List<Integer> cityIds);


    /**
     * 根据时间范围查询入豫记录
     */
    @Query("SELECT i FROM InToHenan i WHERE i.timeId BETWEEN :startTime AND :endTime")
    List<InToHenan> findInToHenanByTimeRange(
            @Param("startTime") Integer startTime,
            @Param("endTime") Integer endTime);

    /**
     * 根据时间范围查询出豫记录
     */
    @Query("SELECT o FROM OutOfHenan o WHERE o.timeId BETWEEN :startTime AND :endTime")
    List<OutOfHenan> findOutOfHenanByTimeRange(
            @Param("startTime") Integer startTime,
            @Param("endTime") Integer endTime);


    /**
     * 统计指定城市作为来源的入豫车辆总数
     */
    @Query("SELECT SUM(i.vehicleCount) FROM InToHenan i WHERE i.fromCityId = :cityId")
    Long sumInFlowByFromCity(@Param("cityId") Integer cityId);

    /**
     * 统计指定城市作为目标的入豫车辆总数
     */
    @Query("SELECT SUM(i.vehicleCount) FROM InToHenan i WHERE i.toCityId = :cityId")
    Long sumInFlowByToCity(@Param("cityId") Integer cityId);

    /**
     * 统计指定城市作为出发地的出豫车辆总数
     */
    @Query("SELECT SUM(o.vehicleCount) FROM OutOfHenan o WHERE o.fromCityId = :cityId")
    Long sumOutFlowByFromCity(@Param("cityId") Integer cityId);

    /**
     * 统计指定城市作为目的地的出豫车辆总数
     */
    @Query("SELECT SUM(o.vehicleCount) FROM OutOfHenan o WHERE o.toCityId = :cityId")
    Long sumOutFlowByToCity(@Param("cityId") Integer cityId);

    /**
     * 统计指定城市的总流量(入豫+出豫)
     */
    @Query("SELECT COALESCE(SUM(i.vehicleCount), 0) + COALESCE(SUM(o.vehicleCount), 0) " +
            "FROM InToHenan i, OutOfHenan o " +
            "WHERE (i.fromCityId = :cityId OR i.toCityId = :cityId) " +
            "OR (o.fromCityId = :cityId OR o.toCityId = :cityId)")
    Long sumTotalFlowByCity(@Param("cityId") Integer cityId);
}
