package com.mee.manage.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.mee.manage.po.Fee;
import com.mee.manage.po.Products;
import com.mee.manage.po.User;
import com.mee.manage.service.IFeeService;
import com.mee.manage.service.IProductsService;
import com.mee.manage.service.ISettleService;
import com.mee.manage.service.ISpecialSkuService;
import com.mee.manage.service.IUserService;
import com.mee.manage.vo.ExpDetailVo;
import com.mee.manage.vo.FeeDetailVo;
import com.mee.manage.vo.OrderVo;
import com.mee.manage.vo.ProductVo;
import com.mee.manage.vo.SettleFeeVo;
import com.mee.manage.vo.SettleVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettleServiceImpl implements ISettleService {

    private static final Logger logger = LoggerFactory.getLogger(ISettleService.class);

    @Autowired
    IUserService userService;

    @Autowired
    IFeeService feeService;

    @Autowired
    ISpecialSkuService specialSkuService;

    @Autowired
    IProductsService productsService;

    User user;


    @Override
    public boolean checkParams(SettleVo settleVo) {
        if(settleVo == null)
            return false;

        boolean result = true; 
        if(settleVo.getUserId() == null && settleVo.getUserName() == null || settleVo.getOrder() == null) {
            result = false;
            logger.info("params is null userId = {}; UserName = {}; Order = {}",
                settleVo.getUserId(),settleVo.getUserName(),
                settleVo.getOrder());
        }

        return result;
    }

    @Override
    public List<SettleFeeVo> getSettleFee(SettleVo settleVo) {
        Long userId = settleVo.getUserId();

        User user = null;
        if(userId != null) {
            user = userService.getUserById(userId);
        }else {
            user = userService.getUserByName(settleVo.getUserName());
        }
        if(user == null)
            return null;
        setUser(user);
        List<OrderVo> orders = settleVo.getOrder();
        if(orders == null)
            return  null;

        Integer userType = user.getType();
        //统计请求的处理时间
        List<Fee> fees = feeService.getFeeList(userType);

        List<SettleFeeVo> settleFees = calculate(fees,orders);

        return settleFees;
    }

    private List<SettleFeeVo> calculate(List<Fee> fees,List<OrderVo> orders){
        if(fees == null || fees.size() <= 0 ||
                orders == null || orders.size() <= 0)
            return null;

        List<Products> products = getAllProducts(orders);

        List<SettleFeeVo> settleFees = new ArrayList<>();
        for (OrderVo order : orders){
            if(order == null)
                continue;

            SettleFeeVo feeVo = new SettleFeeVo();
            feeVo.setOrderId(order.getOrderId());
            feeVo.setExpId(order.getExpId());
            feeVo.setName(order.getName());
            feeVo.setPhone(order.getPhone());
            feeVo.setAddress(order.getAddress());
            feeVo.setProducts(order.getProduct());

            BigDecimal totalFee = BigDecimal.ZERO;

            //add
            List<FeeDetailVo> detailVos = new ArrayList<>();
            for (Fee fee : fees){
                FeeDetailVo detailVo = getFee(fee,order);

                if (detailVo != null) {
                    detailVos.add(detailVo);
                    totalFee = totalFee.add(detailVo.getFee());
                }
            }
            //add 快递费
            FeeDetailVo expFee = getExpFee(order,products);
            if(expFee != null) {
                detailVos.add(expFee);
                totalFee = totalFee.add(expFee.getFee());
            }

            feeVo.setFeeDetail(detailVos);
            feeVo.setTotalFee(totalFee);
            settleFees.add(feeVo);
        }


        return settleFees;
    }

    //获取没有sku的单子
    //返回快递详情
    private FeeDetailVo getExpFee(OrderVo order,List<Products> products) {
        if(order == null)
            return null;

        List<ProductVo> productVos = order.getProduct();
        if(productVos == null)
            return null;

        int total_weight = 0;
        String sender = order.getSender();
        BigDecimal exp_fee  = null;
        if(sender == null || sender.toLowerCase().trim().equals("3pl")){
            exp_fee = new BigDecimal(5.5);
        }else {
            exp_fee = new BigDecimal(5);
        }

        StringBuffer error_SKU = new StringBuffer();
        List<ExpDetailVo> expDetailVoList = new ArrayList<>();

        for(ProductVo pv : productVos){
            ExpDetailVo detail = new ExpDetailVo();
            String sku = pv.getSku();
            int num = pv.getNum();
            detail.setNum(num);
            detail.setSku(sku);
            detail.setName(pv.getContent());
            detail.setOrderId(order.getOrderId());
            detail.setSender(order.getSender());
            detail.setUnitprice(exp_fee);

            int weight = getWeight(Long.parseLong(sku),products);
            if(weight < 0){
                error_SKU.append(sku).append(";");
            }else {
                detail.setWeight(weight);
                detail.setTotalprice(exp_fee.multiply(new BigDecimal(num * weight)).divide(BigDecimal.valueOf(1000)));
                total_weight += num * weight;
            }
            expDetailVoList.add(detail);
        }


        total_weight = total_weight < 1000?1000:total_weight;

        BigDecimal fee = exp_fee.multiply(new BigDecimal(total_weight)).divide(BigDecimal.valueOf(1000));
        FeeDetailVo feeDetailVo = new FeeDetailVo();
        feeDetailVo.setFee(fee);
        feeDetailVo.setFeeType(0);
        feeDetailVo.setFeeTypeName("快递费");
        feeDetailVo.setRemark(error_SKU.toString());
        feeDetailVo.setDetails(expDetailVoList);

        return feeDetailVo;
    }

    private int getWeight(Long sku,List<Products> products){
        int weight = -1;
        if(products == null || products.isEmpty())
            return -1;

        for(Products product : products){
            if(sku.compareTo(product.getSku()) == 0){
                weight = product.getWeight();
            }
        }
        return weight;
    }

    private FeeDetailVo getFee(Fee fee,OrderVo order){
        if(fee == null)
            return null;

        FeeDetailVo detailVo = new FeeDetailVo();
        detailVo.setFeeTypeName(fee.getName());
        detailVo.setFee(BigDecimal.ZERO);
        detailVo.setFeeType(fee.getFeeType());

        if(fee.getKey() == null){
            detailVo.setFee(fee.getFee());
            return detailVo;
        }

        String key = fee.getKey().toLowerCase().trim();
        String condition = fee.getCondition().trim();
        String value = fee.getValue();

        if(key.equals("sku")){
            List<Long> skus = specialSkuService.getSpecialSkuByUserId(user.getId(),Integer.parseInt(fee.getValue()));
            if(skus != null && skus.size() > 0) {
                if(condition.equals("in")){
                    List<Long> inSkus = isInSku(skus,order.getProduct());
                    if(inSkus != null && inSkus.size() > 0){
                        BigDecimal skufee = BigDecimal.ZERO;

                        StringBuffer remark = new StringBuffer();
                        for(Long insku : inSkus){
                            skufee = skufee.add(fee.getFee());
                            remark.append(insku).append(";");
                        }
                        detailVo.setRemark(remark.toString());
                        detailVo.setFee(skufee);
                    }
                }
            }
        }else if(key.equals("num(sku)")){
            boolean isFlag = false;
            int numsku = getSkuNum(order.getProduct());
            if(condition.equals(">")){
                if(numsku > Integer.parseInt(value)){
                    isFlag = true;
                }
            }else if (condition.equals("<")){
                if(numsku < Integer.parseInt(value)){
                    isFlag = true;
                }
            }else if (condition.equals(">=")){
                if(numsku >= Integer.parseInt(value)){
                    isFlag = true;
                }
            }else if (condition.equals("<=")) {
                if(numsku <= Integer.parseInt(value)){
                    isFlag = true;
                }
            }else if (condition.equals("><")){
                String[] valus = value.split(";");
                if(numsku > Integer.parseInt(valus[0]) &&
                        numsku < Integer.parseInt(valus[1])) {
                    isFlag = true;
                }
            }

            if(isFlag){
                detailVo.setFee(fee.getFee());
            }

        }

        return detailVo;
    }

    private int getSkuNum(List<ProductVo> products){
        if(products == null)
            return 0;

        int total = 0;
        for(ProductVo product : products){
            total += product.getNum();
        }
        return total;
    }

    private List<Long> isInSku(List<Long> skus,List<ProductVo> products){
        if(skus == null || skus.size() <= 0 || products == null || products.size() <= 0)
            return null;

        List<Long> productSkus = new ArrayList<>();

        for(ProductVo product : products){
            if(skus.contains(Long.parseLong(product.getSku()))){
                productSkus.add(Long.parseLong(product.getSku()));
            }
        }

        return productSkus;
    }

    private List<Products> getAllProducts(List<OrderVo> orders){
        if(orders == null || orders.isEmpty())
            return null;

        List<Long> skus = new ArrayList<>();

        for (OrderVo order : orders){
            List<ProductVo> productVos = order.getProduct();
            for (ProductVo product : productVos)
                skus.add(Long.parseLong(product.getSku()));
        }

         return productsService.getProductsBySkus(skus);
    }


    private void setUser(User user){
        this.user = user;
    }

}
