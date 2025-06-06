package com.example.pgstudy;

import java.util.List;

public interface DemoTypeMapper {
    void insertDemoType(DemoType demoType);
    List<DemoType> selectAllDemoTypes();
    void updateDemoType(DemoType demoType);
    void deleteDemoTypeById(Integer id);
} 