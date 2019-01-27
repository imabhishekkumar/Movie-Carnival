package com.popularmovies.abhis.popularmovies.Model;

public class CrewData {
    String crewName;
    String crewCharacterName;
    String crewJob;
    String crewProfilePath;
    String crewId;

    public CrewData() {
    }

    public CrewData(String crewName, String crewCharacterName, String crewJob, String crewProfilePath, String crewId) {
        this.crewName = crewName;
        this.crewCharacterName = crewCharacterName;
        this.crewJob = crewJob;
        this.crewProfilePath = crewProfilePath;
        this.crewId = crewId;
    }

    public String getCrewName() {
        return crewName;
    }

    public void setCrewName(String crewName) {
        this.crewName = crewName;
    }

    public String getCrewCharacterName() {
        return crewCharacterName;
    }

    public void setCrewCharacterName(String crewCharacterName) {
        this.crewCharacterName = crewCharacterName;
    }

    public String getCrewJob() {
        return crewJob;
    }

    public void setCrewJob(String crewJob) {
        this.crewJob = crewJob;
    }

    public String getCrewProfilePath() {
        return crewProfilePath;
    }

    public void setCrewProfilePath(String crewProfilePath) {
        this.crewProfilePath = crewProfilePath;
    }

    public String getCrewId() {
        return crewId;
    }

    public void setCrewId(String crewId) {
        this.crewId = crewId;
    }
}
