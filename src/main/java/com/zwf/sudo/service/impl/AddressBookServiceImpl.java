package com.zwf.sudo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwf.sudo.entity.AddressBook;
import com.zwf.sudo.mapper.AddressBookMapper;
import com.zwf.sudo.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
