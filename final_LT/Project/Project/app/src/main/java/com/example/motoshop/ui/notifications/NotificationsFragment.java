package com.example.motoshop.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.example.motoshop.R;
import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Thông báo & Tin tức");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        RecyclerView rvNews = view.findViewById(R.id.rvNews);
        rvNews.setLayoutManager(new LinearLayoutManager(getContext()));
        NewsAdapter adapter = new NewsAdapter(buildNewsList());
        rvNews.setAdapter(adapter);
    }

    private List<NewsItem> buildNewsList() {
        List<NewsItem> list = new ArrayList<>();

        list.add(new NewsItem(
                "Honda Việt Nam",
                "30/06/2025",
                "Honda Việt Nam tổ chức Lễ chào mừng chiếc xe máy thứ 40 triệu",
                "Honda Việt Nam (HVN) tổ chức Lễ chào mừng chiếc xe máy thứ 40 triệu – Dấu ấn quan trọng trong hành trình gần 30 năm đồng hành và phát triển cùng người dân Việt Nam. Kể từ khi thành lập vào năm 1996, HVN luôn cung cấp các sản phẩm và dịch vụ chất lượng tốt nhất với giá thành hợp lý, vượt trên sự mong đợi của khách hàng. Honda Vision là chiếc xe thứ 40 triệu được sản xuất, thể hiện cho những nỗ lực, cống hiến của các cán bộ công nhân viên HVN nhằm cung cấp những mẫu xe máy với chất lượng cao tới người tiêu dùng.",
                "Honda Việt Nam"
        ));

        list.add(new NewsItem(
                "Yamaha Motor Việt Nam",
                "2026",
                "Hội nghị Đại lý 2026: Chung Định Hướng Cùng Thành Công – Move As One, Succeed As One",
                "Không chỉ dừng lại ở một sự kiện thường niên, hội nghị năm nay tại TP. Hồ Chí Minh là dịp để toàn hệ thống cùng nhìn lại hành trình đã qua. Điểm nhấn là sự ra mắt của Yamaha Gear 125 Hybrid – mẫu xe tay ga phổ thông đầu tiên tại Việt Nam được trang bị động cơ Blue Core Hybrid đạt tiêu chuẩn châu Âu. Với khả năng tiết kiệm nhiên liệu nổi bật, thiết kế gọn nhẹ và linh hoạt, Gear hứa hẹn sẽ trở thành lựa chọn tối ưu cho nhu cầu di chuyển hằng ngày.",
                "Yamaha Motor Việt Nam"
        ));

        list.add(new NewsItem(
                "VinFast",
                "03/04/2026",
                "Xe máy điện VinFast lập kỷ lục doanh số chưa từng có",
                "VinFast đã nhận hơn 135.000 đơn đặt hàng, xuất xưởng ra thị trường hơn 93.000 xe máy điện trong tháng 3/2026, đạt mức doanh số tháng cao nhất từ trước đến nay. Trong đó, Evo và Feliz là hai dòng sản phẩm được ưa chuộng nhất. Mức tăng cao kỷ lục cho thấy làn sóng chuyển đổi xanh đang tiến triển mạnh mẽ, đồng thời khẳng định vị thế vững chắc của VinFast trên thị trường xe máy Việt Nam.",
                "VinFast"
        ));

        list.add(new NewsItem(
                "Tư vấn xe",
                "2026",
                "Học sinh có thể sử dụng xe máy 50cc không? Vì sao 50cc phù hợp?",
                "Xe máy 50cc phù hợp cho học sinh, sinh viên vì giá thành hợp lý, tiết kiệm nhiên liệu, dễ điều khiển và an toàn với thiết kế gọn nhẹ, yên thấp. SYM PRITI 50cc là gợi ý hàng đầu với thiết kế hiện đại, động cơ bền bỉ và tiết kiệm xăng tối ưu. Nếu bạn đang tìm kiếm một mẫu xe 50cc chính hãng, giá rẻ và bền bỉ, hãy đến cửa hàng để được tư vấn chi tiết.",
                "Tư vấn & Kiến thức"
        ));

        list.add(new NewsItem(
                "Honda Việt Nam",
                "2025",
                "Honda Việt Nam ra mắt cộng đồng +84 Honda Bikers – Bắt sóng đam mê",
                "+84 Honda Bikers được ra đời, là một cộng đồng toàn diện cho tất cả các Honda biker tại Việt Nam. Đây sẽ là nơi kết nối chủ sở hữu các dòng xe tay côn, xe phân khối lớn trên toàn quốc. Thành viên cộng đồng sẽ được tham gia các sự kiện do HVN tổ chức từ địa phương đến toàn quốc như Honda Thanks Day, Bikers' Rally, Biker Day và nhiều chuyến đi khám phá đất trời thú vị.",
                "Honda Việt Nam"
        ));

        return list;
    }
}
