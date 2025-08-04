package com.xinni.service;

import com.xinni.dto.EmployeeDTO;
import com.xinni.dto.EmployeeFixPwdDTO;
import com.xinni.dto.EmployeeLoginDTO;
import com.xinni.dto.PageDTO;
import com.xinni.entity.Employee;
import com.xinni.result.PageResult;

public interface EmployeeService {
    Employee getEmployeeById(Integer id);

    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void register(EmployeeLoginDTO employeeLoginDTO);

    PageResult employeePageList(PageDTO pageDTO);

    void update(EmployeeDTO employeeDTO);

    void delete(Integer id);

    void onOff(Integer id);

    void addEmployee(EmployeeDTO employeeDTO);

    void fixPwd(EmployeeFixPwdDTO employeeFixPwdDTO);
}
