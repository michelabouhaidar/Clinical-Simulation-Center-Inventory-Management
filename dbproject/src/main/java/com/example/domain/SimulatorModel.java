package com.example.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "SIMULATOR_MODEL")
public class SimulatorModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MODELID", nullable = false)
    private Integer id;

    @Column(name = "MODELNAME", length = 50)
    private String modelName;

    @Column(name = "SPECS", length = 1024)
    private String specs;

    @Column(name = "CALREQ")
    private Boolean calReq;

    @Column(name = "MAXDAYS")
    private Integer maxDays;

    @OneToMany(mappedBy = "model")
    private List<Simulator> simulators = new ArrayList<>();

    public SimulatorModel() {}
    public SimulatorModel(Integer id){ this.id = id; }

    public Integer getId(){ return id; }
    public void setId(Integer id){ this.id = id; }
    public String getModelName(){ return modelName; }
    public void setModelName(String modelName){ this.modelName = modelName; }
    public String getSpecs(){ return specs; }
    public void setSpecs(String specs){ this.specs = specs; }
    public Boolean getCalReq(){ return calReq; }
    public void setCalReq(Boolean calReq){ this.calReq = calReq; }
    public Integer getMaxDays(){ return maxDays; }
    public void setMaxDays(Integer maxDays){ this.maxDays = maxDays; }

    public List<Simulator> getSimulators(){ return simulators; }
    public void addSimulator(Simulator s){ if(!simulators.contains(s)){ simulators.add(s); s.setModel(this);} }
    public void removeSimulator(Simulator s){ if(simulators.remove(s) && s.getModel()==this) s.setModel(null); }
	@Override
    public String toString() {
        return modelName != null ? modelName : ("Model #" + id);
    }

}
