/*
 * peuchabonfilter.h
 *
 *  Created on: 21 mai 2013
 *      Author: theveny
 */

#ifndef PUECHABONFILTER_H_
#define PUECHABONFILTER_H_

#include "shotfilter.h"

class puechabonfilter: public shotfilter {
public:
    puechabonfilter();
    virtual ~puechabonfilter();

    virtual bool shotToKeep(pointcloud* p);
    virtual bool echoToKeep(target& t);
};



#endif /* PUECHABONFILTER_H_ */
