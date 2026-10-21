package com.javaweb.controller.admin;

import com.javaweb.constant.SystemConstant;
import com.javaweb.enums.District;
import com.javaweb.enums.TypeCode;
import com.javaweb.model.dto.BuildingDTO;
import com.javaweb.model.response.BuildingSearchResponse;
import com.javaweb.service.IUserService;
import com.javaweb.service.IBuildingService;
import com.javaweb.utils.DisplayTagUtils;
import com.javaweb.utils.MessageUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller(value = "buildingControllerOfAdmin")
public class BuildingController {
    @Autowired
    private IBuildingService buildingService;
    @Autowired
    private IUserService userService;
    @Autowired
    private MessageUtils messageUtils;

    @GetMapping("/admin/building-list")
    public ModelAndView buildingList(@ModelAttribute("modelSearch") BuildingDTO buildingDTO,
                                     @RequestParam Map<String, Object> params,
                                     @RequestParam(value = "typeCode", required = false) List<String> type,
                                     HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("admin/building/list");
        mav.addObject("staffsList", userService.getStaffs());
        mav.addObject("districts", District.getDistricts());
        mav.addObject("typeCode", TypeCode.getTypeCodes());

        DisplayTagUtils.of(request, buildingDTO);
        int totalItems = buildingService.countTotal(params, type);
        buildingDTO.setTotalItems(totalItems);

        int pageIndex = Math.max(0, buildingDTO.getPage() - 1);
        int pageSize = buildingDTO.getMaxPageItems() > 0 ? buildingDTO.getMaxPageItems() : 10;
        List<BuildingSearchResponse> responseList = buildingService.findAll(params, type, PageRequest.of(pageIndex, pageSize));

        mav.addObject("responseList", responseList);
        mav.addObject("modelSearch", buildingDTO);
        initMessageResponse(mav, request);
        return mav;
    }

    @GetMapping("/admin/building-edit")
    public ModelAndView addBuilding(@ModelAttribute("BuildingEdit") BuildingDTO buildingDTO,
                                    HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("admin/building/edit");
        mav.addObject("districts", District.getDistricts());
        mav.addObject("types", TypeCode.getTypeCodes());
        initMessageResponse(mav, request);
        return mav;
    }

    @GetMapping("/admin/building-edit-{id}")
    public ModelAndView updateBuilding(@PathVariable("id") long id, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("admin/building/edit");
        mav.addObject("BuildingEdit", buildingService.findBuildingById(id));
        mav.addObject("types", TypeCode.getTypeCodes());
        mav.addObject("districts", District.getDistricts());
        initMessageResponse(mav, request);
        return mav;
    }

    private void initMessageResponse(ModelAndView mav, HttpServletRequest request) {
        String message = request.getParameter("message");
        if (StringUtils.isNotBlank(message)) {
            Map<String, String> messageMap = messageUtils.getMessage(message);
            mav.addObject(SystemConstant.ALERT, messageMap.get(SystemConstant.ALERT));
            mav.addObject(SystemConstant.MESSAGE_RESPONSE, messageMap.get(SystemConstant.MESSAGE_RESPONSE));
        }
    }
}
