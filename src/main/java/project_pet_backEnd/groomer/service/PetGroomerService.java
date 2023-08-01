package project_pet_backEnd.groomer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project_pet_backEnd.groomer.dao.PetGroomerDao;
import project_pet_backEnd.groomer.dto.ManagerGetByFunctionIdRequest;
import project_pet_backEnd.groomer.dto.PetGroomerInsertRequest;
import project_pet_backEnd.groomer.vo.PetGroomer;
import project_pet_backEnd.user.dto.ResultResponse;
import project_pet_backEnd.utils.AllDogCatUtils;
import java.util.List;

@Service
public class PetGroomerService {

    @Autowired
    PetGroomerDao petGroomerDao;

    public ResultResponse getManagerByFunctionId(Integer functionId){
        ResultResponse rs = new ResultResponse();
        List<ManagerGetByFunctionIdRequest> managerGetByFunctionIdRequestList =petGroomerDao.getManagerByFunctionId(functionId);
        if(managerGetByFunctionIdRequestList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"未找到擁有美容師個人管理權限之管理員，請至權限管理新增擁有美容師個人管理權限之管理員");
        }
        rs.setMessage(managerGetByFunctionIdRequestList);
        return rs;
    }

    public ResultResponse insertGroomer (PetGroomerInsertRequest petGroomerInsertRequest){

        List<PetGroomer> allGroomer=petGroomerDao.getAllGroomer();
        for (PetGroomer existingGroomer : allGroomer) {
            if (existingGroomer.getManId() == petGroomerInsertRequest.getManId()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "新增失敗，管理員ID重複");
            }
        }
        PetGroomer petGroomer = new PetGroomer();
        petGroomer.setManId(petGroomerInsertRequest.getManId());
        petGroomer.setPgName(petGroomerInsertRequest.getPgName());
        petGroomer.setPgGender(petGroomerInsertRequest.getPgGender());
        petGroomer.setPgPic(AllDogCatUtils.base64Decode(petGroomerInsertRequest.getPgPic()));
        petGroomer.setPgEmail(petGroomerInsertRequest.getPgEmail());
        petGroomer.setPgPh(petGroomerInsertRequest.getPgPh());
        petGroomer.setPgAddress(petGroomerInsertRequest.getPgAddress());
        petGroomer.setPgBirthday(petGroomerInsertRequest.getPgBirthday());

        try{
            petGroomerDao.insertGroomer(petGroomer);
            ResultResponse rs=new ResultResponse();
            rs.setMessage("新增美容師成功");
            return rs;
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "新增失敗，請稍後重試", e);
        }
    }
    public ResultResponse getPetGroomerByManId(Integer manId){
        ResultResponse rs = new ResultResponse();
        PetGroomer petGroomerByManId = petGroomerDao.getPetGroomerByManId(manId);
        if (petGroomerByManId == null) {
            // 沒有找到對應美容師
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "查無此寵物美容師");
        }
        rs.setMessage(petGroomerByManId);
        return rs;
    }


    public ResultResponse getAllGroomer(){
        ResultResponse rs = new ResultResponse();
        List<PetGroomer> allGroomer;
        try {
            allGroomer = petGroomerDao.getAllGroomer();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "獲取寵物美容師列表失敗，請稍後重試", e);
        }

        if (allGroomer == null || allGroomer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "目前無美容師資料");
        }
        rs.setMessage(allGroomer);
        return rs;
    }

    public ResultResponse updateGroomerById(PetGroomer petGroomer) {
        ResultResponse rs = new ResultResponse();
        boolean found = false;
        try {
            // 檢查是否存在該美容師
            List<PetGroomer> allGroomer = petGroomerDao.getAllGroomer();
            for (PetGroomer existingGroomer : allGroomer) {
                if (existingGroomer.getPgId() == petGroomer.getPgId()) {

                    petGroomerDao.updateGroomerById(petGroomer);
                    rs.setMessage("美容師信息更新成功");
                    found = true;
                    break;
                }
            }
        if (!found) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到ID為" + petGroomer.getPgId() + "的美容師");
        }
        return rs;
        } catch (Exception e) {
            // 出現異常，可以拋出異常或返回錯誤提示信息
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "更新美容師信息失敗，請稍後重試", e);
        }
    }
    public ResultResponse getGroomerByPgName(String PgName) {
        ResultResponse rs = new ResultResponse();
        try {
            List<PetGroomer> groomerByPgNameList = petGroomerDao.getGroomerByPgName(PgName);
            if (groomerByPgNameList.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"找不到符合條件的美容師");
            } else {
                rs.setMessage(groomerByPgNameList);
            }
        } catch (Exception e) {
            // If there's an exception, set an error status code (e.g., 500 for Internal Server Error)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "查詢美容師信息失敗，請稍後重試", e);
        }
        return rs;
    }
}
