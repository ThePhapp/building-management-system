package com.javaweb.controller.admin;



import com.javaweb.constant.SystemConstant;
import com.javaweb.enums.District;
import com.javaweb.enums.TypeCode;
import com.javaweb.model.dto.BuildingDTO;
import com.javaweb.model.request.BuildingSearchRequest;
import com.javaweb.model.response.BuildingSearchResponse;
import com.javaweb.service.IUserService;
import com.javaweb.service.impl.BuildingService;
import com.javaweb.utils.DisplayTagUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller(value="buildingControllerOfAdmin")
public class BuildingController {
    @Autowired
    private BuildingService buildingService;
    @Autowired
    private IUserService userService;

    @RequestMapping(value="/admin/building-list", method= RequestMethod.GET)
    public ModelAndView buildingList(@ModelAttribute(SystemConstant.MODEL) BuildingDTO model, @ModelAttribute("modelSearch")BuildingDTO buildingDTO,
                                     @RequestParam Map<String, Object> params,
                                     @RequestParam(value = "typeCode", required = false) List<String> type,
                                     HttpServletRequest request, @ModelAttribute BuildingSearchRequest buildingSearchRequest){
        ModelAndView mav = new ModelAndView("admin/building/list");
        mav.addObject("staffsList", userService.getStaffs());
        mav.addObject("districts", District.getDistricts());
        mav.addObject("typeCode", TypeCode.getTypeCodes());
        DisplayTagUtils.of(request, model);
        List<BuildingSearchResponse> responseList = buildingService.findAll(params, type);
        mav.addObject("responseList", responseList);
        mav.addObject(SystemConstant.MODEL, model);
        return mav;
    }

    @RequestMapping(value = "/admin/building-edit", method = RequestMethod.GET)
    public ModelAndView addBuilding(@ModelAttribute("BuildingEdit") BuildingDTO buildingDTO) {
        ModelAndView mav = new ModelAndView("admin/building/edit");
        mav.addObject("districts", District.getDistricts());
        mav.addObject("types", TypeCode.getTypeCodes());
        return mav;
    }

    @RequestMapping(value = "/admin/building-edit-{id}", method = RequestMethod.GET)
    public ModelAndView updateBuilding(@PathVariable("id") long id, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("admin/building/edit");
        mav.addObject("BuildingEdit", buildingService.findBuildingById(id));
        mav.addObject("types", TypeCode.getTypeCodes());
        mav.addObject("districts", District.getDistricts());
        return mav;
    }

    @RequestMapping(value="/admin/building-list-{id}", method= RequestMethod.GET)
    public ModelAndView deleteBuilding(@ModelAttribute("id")Long id){
        ModelAndView mav = new ModelAndView("admin/building/edit");
        return mav;
    }
}
