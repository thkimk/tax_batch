package com.hanwha.tax.batch.tax.service;

import com.hanwha.tax.batch.Constants;
import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.book.service.BookService;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.entity.CustDeduct;
import com.hanwha.tax.batch.entity.CustFamily;
import com.hanwha.tax.batch.entity.CustInfo;
import com.hanwha.tax.batch.entity.CustInfoDtl;
import com.hanwha.tax.batch.entity.Industry;
import com.hanwha.tax.batch.industry.service.IndustryService;
import com.hanwha.tax.batch.total.service.TotalService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Getter
@Service("calcTax")
public class CalcTax {

    @Autowired
    private CustService custService;

    @Autowired
    private IndustryService industryService;

    @Autowired
    private BookService bookService;

    @Autowired
    private TotalService totalService;

    /*
    기준정보
     */
    private String custId;
    private int year;
    private String bookFlag = "복식부기";
    private Character isNewBusin;
    private String taxFlag = Constants.TAX_FLAG_SBSTR;

    private CustInfo custInfo = null;
    private CustInfoDtl custInfoDtl = null;
    private List<CustFamily> custFamilyList = null;
    private CustDeduct custDeduct = null;
    private Industry industry = null;

    /*
    01.소득(earning) = 수입 - 지출
     */
    private Long earning = 0L;
    private long income = 0L;
    private long outgoing = 0L;
    private long preIncome = 0L;

    /*
    02.과세표준(taxBase) = 01.소득 - 소득공제
     */
    private Long taxBase = 0L;
    private Long deduct = 0L;
    private Long deductMe=0L, deductFamily=0L, deductOthers=0L;

    /*
    03.산출세액(calTax) = 02.과표 * 세율(6%~45%)
     */
    private Long calTax = 0L;
    private Float taxRate = 0f;

    /*
    04.결정세액(decTax)
     */
    private Long decTax = 0L;
    private Long taxDeduct=0L, taxDeductFamily=0L, taxDeductIra=0L;

    /*
    05.최종세액(finTax)
     */
    private Long finTax = 0L;
    private Long addTax = 0L;
    private Long paidTax = 0L;

    /**
     * 소득세 산출 flag 추출
     * @param preIncome : 직전년도 수입 (입력과 마이데이터 존재시, 입력을 우선적으로)
     * @param income : 당해년도 수입
     * @param isNewBusin : 신규 여부 (Y, N)
     */
    public static String taxFlag(Long preIncome, Long income, Character isNewBusin) {
        if (isNewBusin == null) {
            return Constants.TAX_FLAG_NONE;            // No data!!
        }

        // 신규 사업자(Y)
        if (isNewBusin == 'Y') {
            if (income == null) {
                return Constants.TAX_FLAG_NONE;            // No data!!
            }

            // 7500만원 미만
            if (income < 75000000) {
                return Constants.TAX_FLAG_SBSIR;            // 간편, 단순경비
            }
            // 7500만원 이상
            else {
                return Constants.TAX_FLAG_SBSTR;            // 간편, 기준경비
            }
        }
        // 계속 사업자(N)
        else {
            if (preIncome == null) {
                return Constants.TAX_FLAG_NONE;            // No data!!
            }

            // 2400만원 미만
            if (preIncome < 24000000) {
                if (income != null && income >= 75000000) {
                    return Constants.TAX_FLAG_SBSTR;        // 간편, 기준경비
                } else {
                    return Constants.TAX_FLAG_SBSIR;        // 간편, 단순경비
                }
            }
            // 7500만원 미만
            else if (preIncome < 75000000) {
                return Constants.TAX_FLAG_SBSTR;            // 간편, 기준경비
            }
            // 7500만원 이상
            else {
                return Constants.TAX_FLAG_CBSTR;            // 복식, 기준경비
            }
        }
    }

    /**
     * 고객과 연도 기준으로 예상 소득세 제공 (predTax)
     * @param custId
     * @param year
     */
    public void init(String custId, int year) {
        this.custId = custId;
        this.year = year;

        custInfo = custService.getCustInfo(custId).orElse(new CustInfo());
        custInfoDtl = custService.getCustInfoDtl(custId).orElse(new CustInfoDtl());
        custDeduct = custService.getCustDeduct(custId, year).orElse(new CustDeduct());
        custFamilyList = custService.getCustFamilyListByCustId(custId);
        if (!Utils.isEmpty(custInfoDtl.getIndstCode())) industry = industryService.getIndustry(custInfoDtl.getIndstCode()).orElse(new Industry());

        custDeduct.updateNull();

        preIncome = totalService.getTotalIncome(custId, year-1);
        income = totalService.getTotalIncome(custId, year);
        outgoing = totalService.getTotalOutgoing(custId, year);
        isNewBusin = custInfoDtl.getIsNewBusin() == null ? 0 < preIncome ? 'N' : 'Y' : custInfoDtl.getIsNewBusin();
        taxFlag = taxFlag(preIncome, income, isNewBusin);
    }

    void deductMe() {
        deductMe = 1500000L;

        // 장애인
        if (custInfoDtl.getIsDisorder() != null && custInfoDtl.getIsDisorder() == 'Y') deductMe += 2000000;

        // 경로우대자
        if (Utils.taxAge(custInfo.getBirth()) >= 70) deductMe += 1000000;

        // 한부모 공제 : 고객이 직접 체크한 값으로 계산
        boolean isFlag = false;
        if (custInfoDtl.getIsSinParent() != null && custInfoDtl.getIsSinParent() == 'Y') {
            deductMe += 1000000;
            isFlag = true;
        }

        // 부녀자 : 3천만원 소득 이하의 여성으로, (결혼했으면 500,000원, 세대주이고 부양가족이 있으면 500,000, 둘중 하나)
        if (!isFlag) {
            if (custInfo.getGender() == 'F' && earning <= 30000000) {
                if (CustFamily.existPartner(custFamilyList)) {
                    deductMe += 500000;
                } else if (custInfoDtl.getIsHshld() != null && custInfoDtl.getIsHshld() == 'Y' && custFamilyList.size() > 0) {
                    deductMe += 500000;
                }
            }
        }
    }

    void deductFamily() {
        deductFamily = 0L;
        for (final CustFamily custFamily : custFamilyList) {
            int age = Utils.taxAge(custFamily.getBirth());
            if (age >= 60 || age <= 20 || CustFamily.TypeFamily.배우자.getCode().equals(custFamily.getFamily()) || custFamily.getIsDisorder() == 'Y') {
                deductFamily += 1500000;
            }

            if (custFamily.getIsDisorder() == 'Y') deductFamily += 2000000;
            if (age >= 70) deductFamily += 1000000;
        }
    }

    /**
     * 공제금액을 배열로 리턴 ( [0] npc(국민연금), [1] med(소상공인), [2] sed(창업), [3] rsp(개인연금) [4] ira + irp )
     * @param custDeduct
     * @param custInfo
     * @return Long[] : [4] ira + irp
     */
    public static Long[] deductVals(CustDeduct custDeduct, CustInfo custInfo) {
        log.info("#### kkk: deductVals(): {}, {}", custDeduct.toString(), custInfo.toString());
        Long[] vals = new Long[5];
        Long earning = custDeduct.getIncome() - custDeduct.getOutgoing();

        // 국민연금 납입료 전액
        vals[0] = custDeduct.getNpcAmt();

        // 소기업 공제부금
        vals[1] = earning> 100000000? Math.min(custDeduct.getMedAmt(), 2000000):
                earning> 40000000? Math.min(custDeduct.getMedAmt(), 3000000): Math.min(custDeduct.getMedAmt(), 5000000);

        // 중소기업 창업에 출자 (벤처창업 출자)
//        vals[2] = Math.min(earning*(long)(0.5*10) /10, custDeduct.getSedAmt()*(long)(0.1*10) /10);
        Long tmp01 = custDeduct.getSed01Amt() * (long)(0.1*10) /10;
        Long tmp02 = Math.min(custDeduct.getSed02Amt() * (long)(0.1*10) /10, 3000000);
        Long tmp03;
        if (custDeduct.getSed03Amt() <= 30000000) {
            tmp03 = custDeduct.getSed03Amt();
        } else if (custDeduct.getSed03Amt() <= 50000000) {
            tmp03 = custDeduct.getSed03Amt() * (long)(0.7*10) /10;
        } else tmp03 = custDeduct.getSed03Amt() * (long)(0.3*10) /10;
        Long tmp04;
        if (custDeduct.getSed04Amt() <= 30000000) {
            tmp04 = custDeduct.getSed04Amt();
        } else if (custDeduct.getSed04Amt() <= 50000000) {
            tmp04 = custDeduct.getSed04Amt() * (long)(0.7*10) /10;
        } else tmp04 = custDeduct.getSed04Amt() * (long)(0.3*10) /10;

        vals[2] = Math.min(tmp01+tmp02+ tmp03+ tmp04, earning * (long)(0.5*10) /10);

        // 개인연금저축
        vals[3] = Math.min(custDeduct.getRspAmt()*(long)(0.4*10) /10, 720000);   // 한도가 720000원

        // 연금저축 (ira+irp)
        if (earning > 100000000) {
            Long ira1 = Math.min(custDeduct.getIraAmt(), 3000000);
            vals[4] = (ira1 + Math.min(custDeduct.getIrpAmt(), 7000000-ira1)) * (long)(0.15*100) /100;
        } else {
            if (Utils.taxAge(custInfo.getBirth()) >= 50) {
                Long ira1 = Math.min(custDeduct.getIraAmt(), 6000000);
                if (earning > 40000000) {
                    vals[4] = (ira1 + Math.min(custDeduct.getIrpAmt(), 9000000 - ira1)) * (long) (0.12 * 100) / 100;
                } else {
                    vals[4] = (ira1 + Math.min(custDeduct.getIrpAmt(), 9000000 - ira1)) * (long) (0.15 * 100) / 100;
                }
            } else {
                Long ira1 = Math.min(custDeduct.getIraAmt(), 4000000);
                if (earning > 40000000) {
                    vals[4] = (ira1 + Math.min(custDeduct.getIrpAmt(), 7000000 - ira1)) * (long) (0.12 * 100) / 100;
                } else {
                    vals[4] = (ira1 + Math.min(custDeduct.getIrpAmt(), 7000000 - ira1)) * (long) (0.15 * 100) / 100;
                }
            }
        }

        log.info("#### kkk: deductVals(): {}, {}, {}, {}, {}", vals[0], vals[1], vals[2], vals[3], vals[4]);
        return vals;
    }

    void deductOthers() {
        custDeduct.setIncome(income);
        custDeduct.setOutgoing(outgoing);
        Long[] vals = CalcTax.deductVals(custDeduct, custInfo);

        // 세액공제 (연금계좌 : 개인연금/퇴직연금)
        taxDeductIra = vals[4];

        deductOthers =  vals[0]+ vals[1]+ vals[2]+ vals[3];
    }

    /**
     * 공제금액 계산 (소득공제)
     * 본인 및 부양가족
     * 공제항목
     */
    void deduct() {
        // 본인 공제
        deductMe();

        // 부양가족 공제
        deductFamily();

        // 기타 공제
        deductOthers();

        deduct = deductMe + deductFamily + deductOthers;
        log.debug("-- [2.1] 소득공제 : {} = {} + {} + {}", deduct, deductMe, deductFamily, deductOthers);
    }

    /**
     * 과세표준 세율
     * @param taxBase : 과세표준
     * @return : %
     */
//    public static float taxRate(Long taxBase) {
//        float res = 0f;
//        if (taxBase <= 12000000) res = 6;
//        else if (taxBase <=  46000000) res = 15;
//        else if (taxBase <=  88000000) res = 24;
//        else if (taxBase <= 150000000) res = 35;
//        else if (taxBase <= 120000000) res = 38;
//        else if (taxBase <= 300000000) res = 40;
//        else if (taxBase <= 500000000) res = 42;
//        else res = 45;
//
//        return (res / 100);
//    }
    public static Long taxRateValue(Long taxBase) {
        if (taxBase <= 12000000) {
            return taxBase*6 /100;
        }
        else if (taxBase <=  46000000) {
            return taxRateValue(12000000L) + (taxBase-12000000)*15 /100;
        }
        else if (taxBase <=  88000000) {
            return taxRateValue(46000000L) + (taxBase-46000000)*24 /100;
        }
        else if (taxBase <= 150000000) {
            return taxRateValue(88000000L) + (taxBase-88000000)*35 /100;
        }
        else if (taxBase <= 300000000) {
            return taxRateValue(150000000L) + (taxBase-150000000)*38 /100;
        }
        else if (taxBase <= 500000000) {
            return taxRateValue(300000000L) + (taxBase-300000000)*40 /100;
        }
        else if (taxBase <= 1000000000) {
            return taxRateValue(500000000L) + (taxBase-500000000)*42 /100;
        }
        else
            return taxRateValue(1000000000L) + (taxBase-1000000000)*45 /100;
    }

    void taxDeduct() {
        // 자녀세액 공제 : 만7세 이상 자녀
        // 당해년도 출생신고 : 한국나이 1세 자녀, 300000/ 500000/ 700000
        taxDeductFamily = 0L;
        int count = 0, countBorn = 0;
        for (final CustFamily custFamily : custFamilyList) {
            if (CustFamily.TypeFamily.자녀.getCode().equals(custFamily.getFamily()) && Utils.taxAge(custFamily.getBirth()) >= 7) {
                count++;
                if (count < 3) {
                    taxDeductFamily += 150000;
                }
            }

            if (CustFamily.TypeFamily.자녀.getCode().equals(custFamily.getFamily()) && Utils.koreaAge(custFamily.getBirth()) == 1) {
                countBorn++;
                if (countBorn == 1) taxDeductFamily += 300000;
                else if (countBorn == 2) taxDeductFamily += 500000;
                else taxDeductFamily += 700000;
            }
        }
        if (count >= 3) {
            taxDeductFamily += 300000 + (count-3)/2 * 600000;
        }

        // 연금계좌 세액공제 : IRP는? --> Mydata 제공 IRP??
        // Tax.deductVals() 의 5번째 값[4]

        // 표준 세액공제 : 70000
        // 전자 세액공제 : 20000
        taxDeduct = 70000 + 20000 + taxDeductFamily + taxDeductIra;
    }

    void finTax() {
        // 경비율 가산세 : 계속사업자이고 직전년도 수입이 4800만 이상인 경우 산출세액의 20%
        // 간편장부 가산세 : 비적격증빙 경비 합의 2%%
//        log.info("#### finTax(): {}, {}", isNewBusin, preIncome);
        if (!"간편장부".equals(bookFlag)) {
            if (isNewBusin == 'N' && preIncome >= 48000000) {
                addTax = calTax * 2 / 10;
            }
        }

        // 기납부세액 : 수입금액의 3% --> income에는 모두 3% 기납부세액이 포함되어있어야 함 (미포함시, income계산때 강제 추가)
//        Long income33 = 0L;
//        if (simTaxVo == null && predTaxVo == null) income33 = totalIncomeRepository.selectRtIncome33(cid, year);
//        else income33 = income;
//        paidTax = (income33 * 3) /100;
        paidTax = (income * 3) /100;
    }

    /**
     * 예상 소득세 계산기
     * @return : 예상 소득세
     */
    public Long calTax() {
        // 01.소득 계산
        // income : totalIncome이 아니고, 계산시에는 실시간 수입 (배치처리 필요 : mydata와 book상의 income을 합산)
        // outgoing : flag 선택값에 따라 다름
        earning = income - outgoing;
        log.info("## [1] 소득 : {} = {} - {}", earning, income, outgoing);

        // 02.과세표준 계산
        // deduct(소득공제) 계산 : 본인, 부양가족, 기타
        deduct();
        taxBase = earning - deduct;
        if (taxBase < 0) taxBase = 0L;
        log.info("## [2] 과표 : {} = {} - {}", taxBase, earning, deduct);

        // 03.산출세액 계산
//        taxRate = taxRate(taxBase);
//        calTax = taxBase * (long)(taxRate*100) /100;
        calTax = taxRateValue(taxBase);
        log.info("## [3] 산출 : {} = {} * {}", calTax, taxBase, taxRate);

        // 04.결정세액 계산
        taxDeduct();
        decTax = calTax - taxDeduct;
        if (decTax < 0) decTax = 0L;
        log.info("## [4] 결정 : {} = {} - {}", decTax, calTax, taxDeduct);

        // 05.최종세액 계산
        finTax();
        finTax = decTax + addTax - paidTax;
        finTax = (finTax / 10) * 10;
//        if (finTax < 0) finTax = 0L;
        log.info("## [5] 최종 : {} = {} + {} - {}", finTax, decTax, addTax, paidTax);

        // 3.3세액
        return finTax;
    }

    public Long calRateTax() {
        log.debug("## 소득세 계산(calRateTax) : {}, taxFlag {}", custId, taxFlag);

        // 업종 선택이 안된 경우 경비율 기반 소득세 0 리턴
        if (industry == null) {
            return 0L;
        }

        // 경비율 기반으로, 지출 재계산
        if ((Integer.parseInt(taxFlag)%10) == 1) {
            bookFlag = "단순경비율";
            Float bookRate = industry.getSimpleExrt();
            Float bookRateExc = industry.getSimpleExrtExc();
            outgoing = (Math.min(income, 40000000) * (long)(bookRate*100) + Math.max(income-40000000, 0) * (long)(bookRateExc*100))/100 /100;
        } else {
            bookFlag = "기준경비율";
            Float bookRate = industry.getStandardExrt();
            outgoing = (long)((income * (long)(bookRate*100))/100 /100);
        }

        return calTax();
    }

    public Long calBookTax() {
        log.info("## 소득세 계산(calBookTax) : CustId {}, taxFlag {}", custId, taxFlag);
        bookFlag = "간편장부";

        if (custId != null) {
            long tmp = bookService.getInvalByYear(custId, year);
            addTax = tmp * 2 /100;
        }

        return calTax();
    }
}
