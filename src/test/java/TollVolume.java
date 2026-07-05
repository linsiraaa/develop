import java.util.List;

public class TollVolume {
    private String name; // 收费站名称
    private Double amount; // 收费金额
    private Double change; // 较昨日变化百分比
    private Integer rank; // 排名
    private List<Integer> vehicleTypes; // 车辆类型占比
    private String changeType; // 变化类型，如 'up', 'down', 'new'

    // 以下是 getter 和 setter 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getChange() {
        return change;
    }

    public void setChange(Double change) {
        this.change = change;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public List<Integer> getVehicleTypes() {
        return vehicleTypes;
    }

    public void setVehicleTypes(List<Integer> vehicleTypes) {
        this.vehicleTypes = vehicleTypes;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    // 计算排名变化
    public Integer calculateRankChange(Integer previousRank) {
        if (previousRank == null) {
            return null;
        }
        return previousRank - this.rank;
    }
}